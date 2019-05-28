package org.runestar.cache.tools;

import org.runestar.cache.format.Index;
import org.runestar.cache.format.Cache;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;

public final class MemCache {

    private final Cache cache;

    private final NavigableMap<Integer, Archive> archives = new TreeMap<>();

    private MemCache(Cache cache) {
        this.cache = cache;
        var archiveCount = cache.getArchiveCount().join();
        for (var i = 0; i < archiveCount; i++) {
            var ia = cache.getIndex(i).join();
            var archive = new Archive(i, ia.version);
            archives.put(i, archive);

            for (var aa : ia.groups) {
                var group = new Group(archive, aa.id, aa.nameHash, aa.version);
                archive.groups.put(aa.id, group);

                for (var fi : aa.files) {
                    var file = new File(group, fi.id, fi.nameHash);
                    group.files.put(fi.id, file);
                }
            }
        }
    }

    public NavigableSet<Integer> archiveIds() {
        return archives.navigableKeySet();
    }

    public Collection<Archive> archives() {
        return archives.values();
    }

    public Archive archive(int archive) {
        return archives.get(archive);
    }

    public static MemCache of(Cache cache) {
        return new MemCache(cache);
    }

    public static final class Archive {

        private final int id;

        private final int version;

        private final NavigableMap<Integer, Group> groups = new TreeMap<>();

        Archive(int id, int version) {
            this.id = id;
            this.version = version;
        }

        public NavigableSet<Integer> groupIds() {
            return groups.navigableKeySet();
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

        private final int version;

        private final NavigableMap<Integer, File> files = new TreeMap<>();

        Group(Archive archive, int id, int nameHash, int version) {
            this.archive = archive;
            this.id = id;
            this.nameHash = nameHash;
            this.version = version;
        }

        public int id() {
            return id;
        }

        public NavigableSet<Integer> fileIds() {
            return files.navigableKeySet();
        }

        public Collection<File> files() {
            return files.values();
        }

        public File file(int file) {
            return files.get(file);
        }

        public ByteBuffer data() {
            if (files.size() != 1) throw new IllegalStateException();
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

        public ByteBuffer data() {
            if (data == null) {
                var groupData = cache.getGroup(group.archive.id, group.id, null).join();
                var filesSplit = Index.Group.split(groupData, group.files().size());
                var fileIdsItr = group.fileIds().iterator();
                for (var f : filesSplit) {
                    group.files.get(fileIdsItr.next()).data = f;
                }
            }
            return data.duplicate();
        }
    }
}
