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
        return getGroupCompressed(archive, group).thenApply(a -> a == null ? null : Compressor.decompress(a, key));
    }

    default CompletableFuture<MasterIndex> getMasterIndex() {
        return getGroup(MASTER_ARCHIVE, MASTER_ARCHIVE).thenApply(MasterIndex::decode);
    }

    default CompletableFuture<Index> getIndex(int archive) {
        return getGroup(MASTER_ARCHIVE, archive).thenApply(a -> a == null ? null : Index.decode(a));
    }

    default CompletableFuture<Integer> getArchiveCount() {
        return getMasterIndex().thenApply(mi -> mi.indices.length);
    }

    static CompletableFuture<Void> update(Cache src, MutableCache dst) {
        return src.getMasterIndex().thenCombine(dst.getMasterIndex(), (sm, dm) -> {
            var fs = new ArrayList<CompletableFuture<Void>>();
            for (var i = 0; i < sm.indices.length; i++) {
                if (i >= dm.indices.length || !sm.indices[i].equals(dm.indices[i])) {
                    fs.add(update(src, dst, i));
                }
            }
            return CompletableFuture.allOf(fs.toArray(new CompletableFuture[0]));
        }).thenCompose(Function.identity());
    }

    static CompletableFuture<Void> update(Cache src, MutableCache dst, int archive) {
        return src.getGroupCompressed(MASTER_ARCHIVE, archive)
                .thenCombine(dst.getGroupCompressed(MASTER_ARCHIVE, archive), (sgc, dgc) ->  {
                    var si = Index.decode(Compressor.decompress(sgc.duplicate()));
                    var di = dgc == null ? null : Index.decode(Compressor.decompress(dgc.duplicate()));
                    var fs = new ArrayList<CompletableFuture<Void>>(si.groups.length + 1);
                    for (var a = 0; a < si.groups.length; a++) {
                        var sig = si.groups[a];
                        var dig = (dgc == null || a >= di.groups.length) ? null : di.groups[a];
                        if (dig == null || sig.version != dig.version || sig.crc != dig.crc) {
                            fs.add(transfer(src, dst, archive, sig.id));
                        } else {
                            fs.add(update(src, dst, archive, sig.id, sig.crc));
                        }
                    }
                    if (!sgc.equals(dgc)) {
                        fs.add(dst.setGroupCompressed(MASTER_ARCHIVE, archive, sgc));
                    }
                    return CompletableFuture.allOf(fs.toArray(new CompletableFuture[0]));
                })
                .thenCompose(Function.identity());
    }

    private static CompletableFuture<Void> update(Cache src, MutableCache dst, int archive, int group, int crc) {
        return dst.getGroupCompressed(archive, group)
                .thenCompose(a -> a == null || crc != IO.crc(a) ? transfer(src, dst, archive, group) : CompletableFuture.completedFuture(null));
    }

    private static CompletableFuture<Void> transfer(Cache src, MutableCache dst, int archive, int group) {
        return src.getGroupCompressed(archive, group).thenCompose(a -> dst.setGroupCompressed(archive, group, a));
    }
}
