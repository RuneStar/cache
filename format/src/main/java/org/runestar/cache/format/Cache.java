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

    default CompletableFuture<MasterIndex> getMasterIndex() {
        return getGroup(MASTER_ARCHIVE, MASTER_ARCHIVE).thenApply(MasterIndex::decode);
    }

    default CompletableFuture<Index> getIndex(int archive) {
        return getGroup(MASTER_ARCHIVE, archive).thenApply(g -> g == null ? null : Index.decode(g));
    }

    default CompletableFuture<Integer> getArchiveCount() {
        return getMasterIndex().thenApply(mi -> mi.indices.length);
    }

    static CompletableFuture<Void> update(Cache src, MutableCache dst) {
        return src.getMasterIndex().thenCombine(dst.getMasterIndex(), (sm, dm) -> {
            var fs = new ArrayList<CompletableFuture<Void>>();
            for (int i = 0; i < sm.indices.length; i++) {
                if (i >= dm.indices.length || !sm.indices[i].equals(dm.indices[i])) {
                    fs.add(update(src, dst, i));
                }
            }
            return IO.allOf(fs);
        }).thenCompose(Function.identity());
    }

    static CompletableFuture<Void> update(Cache src, MutableCache dst, int archive) {
        return src.getGroupCompressed(MASTER_ARCHIVE, archive)
                .thenCombine(dst.getGroupCompressed(MASTER_ARCHIVE, archive), (sgc, dgc) ->  {
                    var si = Index.decode(Compressor.decompress(sgc.duplicate()));
                    var di = dgc == null ? null : Index.decode(Compressor.decompress(dgc.duplicate()));
                    var fs = new ArrayList<CompletableFuture<Void>>();
                    int dj = 0;
                    for (int sj = 0; sj < si.groups.length; sj++) {
                        var sig = si.groups[sj];
                        Index.Group dig = null;
                        while (di != null && dj < di.groups.length) {
                            var g = di.groups[dj++];
                            if (sig.id == g.id) {
                                dig = g;
                                break;
                            } else if (sig.id < g.id) {
                                dj--;
                                break;
                            }
                        }
                        if (dig == null || sig.version != dig.version || sig.crc != dig.crc) {
                            fs.add(src.getGroupCompressed(archive, sig.id).thenCompose(gc -> dst.setGroupCompressed(archive, sig.id, gc)));
                        }
                    }
                    if (di == null || si.version != di.version || !sgc.equals(dgc)) {
                        fs.add(dst.setGroupCompressed(MASTER_ARCHIVE, archive, sgc));
                    }
                    return IO.allOf(fs);
                })
                .thenCompose(Function.identity());
    }
}
