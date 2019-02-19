package org.runestar.cache.tools;

import org.runestar.cache.format.IndexAttributes;
import org.runestar.cache.format.ReadableStore;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class MemCache {

    private final ReadableStore store;

    private final Map<Integer, IndexAttributes> indexAttributes;

    private final Map<Integer, Map<Integer, IndexAttributes.ArchiveAttributes>> archiveAttributes;

    private final Map<Integer, Map<Integer, Map<Integer, IndexAttributes.FileAttributes>>> fileAttributes;

    private final Map<Integer, Map<Integer, Map<Integer, ByteBuffer>>> files;

    private MemCache(ReadableStore store) {
        this.store = store;
        var indexCount = store.getIndexCount().join();
        indexAttributes = new LinkedHashMap<>(indexCount);
        archiveAttributes = new LinkedHashMap<>(indexCount);
        fileAttributes = new LinkedHashMap<>(indexCount);
        files = new HashMap<>(indexCount);
        for (var i = 0; i < indexCount; i++) {
            var ia = store.getIndexAttributes(i).join();
            indexAttributes.put(i, ia);
            files.put(i, new HashMap<>());

            var aai = new LinkedHashMap<Integer, IndexAttributes.ArchiveAttributes>();
            archiveAttributes.put(i, aai);

            var fai = new LinkedHashMap<Integer, Map<Integer, IndexAttributes.FileAttributes>>();
            fileAttributes.put(i, fai);

            for (var ai : ia.archives) {
                aai.put(ai.id, ai);

                var fff = new LinkedHashMap<Integer, IndexAttributes.FileAttributes>();
                fai.put(ai.id, fff);
                for (var fa : ai.files) {
                    fff.put(fa.id, fa);
                }
            }
        }
    }

    public Set<Integer> getIndexIds() {
        return indexAttributes.keySet();
    }

    public Set<Integer> getArchiveIds(int index) {
        return archiveAttributes.get(index).keySet();
    }

    public Set<Integer> getFileIds(int index, int archive) {
        return fileAttributes.get(index).get(archive).keySet();
    }

    public ByteBuffer getArchive(int index, int archive) {
        return getFile(index, archive, 0);
    }

    public ByteBuffer getFile(int index, int archive, int file) {
        return getArchive0(index, archive).get(file).duplicate();
    }

    private Map<Integer, ByteBuffer> getArchive0(int index, int archive) {
        var i = files.get(index);
        var a = i.get(archive);
        if (a == null) {
            var aa = archiveAttributes.get(index).get(archive);
            var fileIds = fileAttributes.get(index).get(archive).keySet();
            var buf = store.getArchive(index, archive, null).join();
            var bufSplit = aa.split(buf);
            var files = new HashMap<Integer, ByteBuffer>(fileIds.size());
            var fileIdsItr = fileIds.iterator();
            for (var file : bufSplit) {
                files.put(fileIdsItr.next(), file);
            }
            i.put(archive, a = files);
        }
        return a;
    }

    public static MemCache of(ReadableStore store) {
        return new MemCache(store);
    }
}
