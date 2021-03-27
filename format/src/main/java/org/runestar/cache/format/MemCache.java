package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.TreeMap;

public final class MemCache {

    private final LocalCache cache;

    private final TreeMap<Integer, Archive> archives = new TreeMap<>();

    private MemCache(LocalCache cache) {
        this.cache = cache;
        var archiveCount = cache.getArchiveCount();
        for (int archiveId = 0; archiveId < archiveCount; archiveId++) {
            var index = cache.getIndex(archiveId);
            if (index == null) continue;
            var archive = new Archive(archiveId, index.version);
            archives.put(archiveId, archive);
            for (var g : index.groups) {
                var group = new Group(archive, g.id, g.nameHash, g.crc32, g.version);
                archive.groups.put(g.id, group);
                for (var f : g.files) {
                    var file = new File(group, f.id, f.nameHash);
                    group.files.put(f.id, file);
                }
            }
        }
    }

    public Collection<Archive> archives() {
        return archives.values();
    }

    public Archive archive(int archive) {
        return archives.get(archive);
    }

    public static MemCache of(LocalCache cache) {
        return new MemCache(cache);
    }

    public static final class Archive {

        private final int id;

        private final int version;

        private final TreeMap<Integer, Group> groups = new TreeMap<>();

        Archive(int id, int version) {
            this.id = id;
            this.version = version;
        }

        public int id() {
            return id;
        }

        public int version() {
            return version;
        }

        public Collection<Group> groups() {
            return groups.values();
        }

        public Group group(int group) {
            return groups.get(group);
        }
    }

    public static final class Group {

        private final Archive archive;

        private final int id;

        private final int nameHash;

        private final int crc32;

        private final int version;

        private int[] key;

        private final TreeMap<Integer, File> files = new TreeMap<>();

        Group(Archive archive, int id, int nameHash, int crc32, int version) {
            this.archive = archive;
            this.id = id;
            this.nameHash = nameHash;
            this.crc32 = crc32;
            this.version = version;
        }

        public int id() {
            return id;
        }

        public int nameHash() {
            return nameHash;
        }

        public int crc32() {
            return crc32;
        }

        public int version() {
            return version;
        }

        public void key(int[] key) {
            this.key = key;
        }

        public int[] key() {
            return key;
        }

        public Collection<File> files() {
            return files.values();
        }

        public File file(int file) {
            return files.get(file);
        }

        public ByteBuffer data() {
            if (files.size() != 1) throw new UnsupportedOperationException();
            return file(0).data();
        }
    }

    public final class File {

        private final Group group;

        private final int id;

        private final int nameHash;

        private ByteBuffer data = null;

        File(Group group, int id, int nameHash) {
            this.group = group;
            this.id = id;
            this.nameHash = nameHash;
        }

        public int id() {
            return id;
        }

        public int nameHash() {
            return nameHash;
        }

        public ByteBuffer data() {
            if (data == null) {
                var g = cache.getGroup(group.archive.id, group.id, group.key);
                if (g == null) return null;
                var dataSplit = Index.Group.split(g.data.duplicate(), group.files().size());
                var files = group.files().iterator();
                for (var d : dataSplit) {
                    files.next().data = d;
                }
            }
            return data.duplicate();
        }
    }
}
