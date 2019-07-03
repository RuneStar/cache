package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface Cache {

    int MASTER_ARCHIVE = 255;

    CompletableFuture<ByteBuffer> getGroupCompressed(int archive, int group);

    default CompletableFuture<ByteBuffer> getGroup(int archive, int group) {
        return getGroup(archive, group, null);
    }

    default CompletableFuture<ByteBuffer> getGroup(int archive, int group, int[] key) {
        return getGroupCompressed(archive, group).thenApply(gc -> gc == null ? null : Compressor.decompress(gc, key));
    }

    default CompletableFuture<IndexMaster[]> getMasterIndex() {
        return getGroup(MASTER_ARCHIVE, MASTER_ARCHIVE).thenApply(IndexMaster::decodeAll);
    }

    default CompletableFuture<Index> getIndex(int archive) {
        return getGroup(MASTER_ARCHIVE, archive).thenApply(g -> g == null ? null : Index.decode(g));
    }

    default CompletableFuture<Integer> getArchiveCount() {
        return getMasterIndex().thenApply(mi -> mi.length);
    }

    static CompletableFuture<Void> update(Cache src, MutableCache dst) {
        return src.getMasterIndex().thenCombine(dst.getMasterIndex(), (sm, dm) -> {
            var fs = new ArrayList<CompletableFuture<Void>>();
            for (int i = 0; i < sm.length; i++) {
                var sim = sm[i];
                var dim = i >= dm.length ? null : dm[i];
                if (!sim.equals(dim)) {
                    fs.add(updateArchive(src, dst, i, sim, dim));
                }
            }
            return IO.allOf(fs);
        }).thenCompose(Function.identity());
    }

    static CompletableFuture<Void> update(Cache src, MutableCache dst, int archive) {
        return updateArchive(src, dst, archive, null, null);
    }

    private static CompletableFuture<IndexMaster> buildIndexMaster(Cache cache, int archive, IndexMaster im) {
        if (im != null && im.groupCompressed != null) return CompletableFuture.completedFuture(im);
        return cache.getGroupCompressed(MASTER_ARCHIVE, archive).thenApply(gc -> gc == null ? null : IndexMaster.decode(gc));
    }

    private static CompletableFuture<Void> updateArchive(
            Cache src,
            MutableCache dst,
            int archive,
            IndexMaster sim,
            IndexMaster dim
    ) {
        return buildIndexMaster(src, archive, sim)
                .thenCombine(buildIndexMaster(dst, archive, dim), (sim2, dim2) -> updateArchive0(src, dst, archive, sim2, dim2))
                .thenCompose(Function.identity());
    }

    private static CompletableFuture<Void> updateArchive0(
            Cache src,
            MutableCache dst,
            int archive,
            IndexMaster sim,
            IndexMaster dim
    ) {
        if (sim.equals(dim)) return CompletableFuture.completedFuture(null);
        var fs = new ArrayList<CompletableFuture<Void>>();
        int dj = 0;
        for (var sg : sim.index.groups) {
            Index.Group dg = null;
            while (dim != null && dj < dim.index.groups.length) {
                var g = dim.index.groups[dj++];
                if (sg.id == g.id) {
                    dg = g;
                    break;
                } else if (sg.id < g.id) {
                    dj--;
                    break;
                }
            }
            if (dg == null || sg.version != dg.version || sg.crc != dg.crc) {
                fs.add(src.getGroupCompressed(archive, sg.id).thenCompose(gc -> dst.setGroupCompressed(archive, sg.id, gc)));
            }
        }
        fs.add(dst.setGroupCompressed(MASTER_ARCHIVE, archive, sim.groupCompressed));
        return IO.allOf(fs);
    }
}
