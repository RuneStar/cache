package org.runestar.cache.tools;

import org.runestar.cache.format.IndexAttributes;
import org.runestar.cache.format.ReadableStore;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;

public final class MemCache {

    private final ReadableStore store;

    private final NavigableMap<Integer, Index> indices = new TreeMap<>();

    private MemCache(ReadableStore store) {
        this.store = store;
        var indexCount = store.getIndexCount().join();
        for (var i = 0; i < indexCount; i++) {
            var ia = store.getIndexAttributes(i).join();
            var index = new Index(i, ia);
            indices.put(i, index);

            for (var aa : ia.archives) {
                var archive = new Archive(index, aa);
                index.archives.put(aa.id, archive);

                for (var fi : aa.files) {
                    var file = new File(archive, fi);
                    archive.files.put(fi.id, file);
                }
            }
        }
    }

    public NavigableSet<Integer> indexIds() {
        return indices.navigableKeySet();
    }

    public Collection<Index> indices() {
        return indices.values();
    }

    public Index index(int index) {
        return indices.get(index);
    }

    public static MemCache of(ReadableStore store) {
        return new MemCache(store);
    }

    public static final class Index {

        private final int id;

        private final IndexAttributes attributes;

        private final NavigableMap<Integer, Archive> archives = new TreeMap<>();

        Index(int id, IndexAttributes attributes) {
            this.id = id;
            this.attributes = attributes;
        }

        public IndexAttributes attributes() {
            return attributes;
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
    }

    public static final class Archive {

        private final Index index;

        private final IndexAttributes.ArchiveAttributes attributes;

        private final NavigableMap<Integer, File> files = new TreeMap<>();

        Archive(Index index, IndexAttributes.ArchiveAttributes attributes) {
            this.index = index;
            this.attributes = attributes;
        }

        public int id() {
            return attributes.id;
        }

        public IndexAttributes.ArchiveAttributes attributes() {
            return attributes;
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

        private final Archive archive;

        private final IndexAttributes.FileAttributes attributes;

        private ByteBuffer data = null;

        File(Archive archive, IndexAttributes.FileAttributes attributes) {
            this.archive = archive;
            this.attributes = attributes;
        }

        public int id() {
            return attributes.id;
        }

        public IndexAttributes.FileAttributes attributes() {
            return attributes;
        }

        public ByteBuffer data() {
            if (data == null) {
                var archiveData = store.getArchive(archive.index.id, archive.attributes.id, null).join();
                var filesSplit = archive.attributes.split(archiveData);
                var fileIdsItr = archive.fileIds().iterator();
                for (var f : filesSplit) {
                    archive.files.get(fileIdsItr.next()).data = f;
                }
            }
            return data.duplicate();
        }
    }
}
