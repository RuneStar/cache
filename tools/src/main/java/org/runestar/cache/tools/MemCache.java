package org.runestar.cache.tools;

import org.runestar.cache.format.Index;
import org.runestar.cache.format.ReadableCache;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;

public final class MemCache {

    private final ReadableCache cache;

    private final NavigableMap<Integer, Archive> archives = new TreeMap<>();

    private MemCache(ReadableCache cache) {
        this.cache = cache;
        var archiveCount = cache.getArchiveCount().join();
        for (var i = 0; i < archiveCount; i++) {
            var ia = cache.getIndex(i).join();
            var archive = new Archive(i, ia);
            archives.put(i, archive);

            for (var aa : ia.groups) {
                var group = new Group(archive, aa);
                archive.groups.put(aa.id, group);

                for (var fi : aa.files) {
                    var file = new File(group, fi);
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

    public static MemCache of(ReadableCache cache) {
        return new MemCache(cache);
    }

    public static final class Archive {

        private final int id;

        private final Index index;

        private final NavigableMap<Integer, Group> groups = new TreeMap<>();

        Archive(int id, Index index) {
            this.id = id;
            this.index = index;
        }

        public Index index() {
            return index;
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

        private final Index.Group attr;

        private final NavigableMap<Integer, File> files = new TreeMap<>();

        Group(Archive archive, Index.Group attr) {
            this.archive = archive;
            this.attr = attr;
        }

        public int id() {
            return attr.id;
        }

        public Index.Group attr() {
            return attr;
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

        private final Index.File attr;

        private ByteBuffer data = null;

        File(Group group, Index.File attr) {
            this.group = group;
            this.attr = attr;
        }

        public int id() {
            return attr.id;
        }

        public Index.File attr() {
            return attr;
        }

        public ByteBuffer data() {
            if (data == null) {
                var groupData = cache.getGroup(group.archive.id, group.attr.id, null).join();
                var filesSplit = group.attr.split(groupData);
                var fileIdsItr = group.fileIds().iterator();
                for (var f : filesSplit) {
                    group.files.get(fileIdsItr.next()).data = f;
                }
            }
            return data.duplicate();
        }
    }
}
