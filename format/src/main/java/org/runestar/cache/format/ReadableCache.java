package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface ReadableCache {

    int MASTER_ARCHIVE = 255;

    CompletableFuture<ByteBuffer> getGroupCompressed(int archive, int group);

    default CompletableFuture<ByteBuffer> getGroup(int archive, int group) {
        return getGroup(archive, group, null);
    }

    default CompletableFuture<ByteBuffer> getGroup(int archive, int group, int[] key) {
        return getGroupCompressed(archive, group).thenApply(a -> a == null ? null : Compressor.decompress(a, key));
    }

    default CompletableFuture<Void> update(WritableCache dst) {
        return getMasterIndex()
                .thenCombine(dst.getMasterIndex(), (smi, dmi) -> {
                    var smis = smi.indices;
                    var dmis = dmi.indices;
                    var fs = new ArrayList<CompletableFuture<Void>>();
                    for (var i = 0; i < smis.length; i++) {
                        if (i >= dmis.length || !smis[i].equals(dmis[i])) {
                            fs.add(update(dst, i));
                        }
                    }
                    return CompletableFuture.allOf(fs.toArray(new CompletableFuture[0]));
                })
                .thenCompose(Function.identity());
    }

    default CompletableFuture<Void> update(WritableCache dst, int archive) {
        return getGroupCompressed(MASTER_ARCHIVE, archive)
                .thenCombine(dst.getGroupCompressed(MASTER_ARCHIVE, archive), (sgc, dgc) ->  {
                    var si = Index.read(Compressor.decompress(sgc.duplicate()));
                    var di = dgc == null ? null : Index.read(Compressor.decompress(dgc.duplicate()));
                    var fs = new ArrayList<CompletableFuture<Void>>(si.groups.length + 1);
                    for (var a = 0; a < si.groups.length; a++) {
                        var sig = si.groups[a];
                        var dig = (dgc == null || a >= di.groups.length) ? null : di.groups[a];
                        if (dig == null || sig.version != dig.version || sig.crc != dig.crc) {
                            fs.add(download(dst, archive, sig.id));
                        } else {
                            fs.add(update(dst, archive, sig.id, sig.crc));
                        }
                    }
                    if (!sgc.equals(dgc)) {
                        fs.add(dst.setGroupCompressed(MASTER_ARCHIVE, archive, sgc));
                    }
                    return CompletableFuture.allOf(fs.toArray(new CompletableFuture[0]));
                })
                .thenCompose(Function.identity());
    }

    private CompletableFuture<Void> update(WritableCache dst, int archive, int group, int crc) {
        return dst.getGroupCompressed(archive, group)
                .thenCompose(a -> a == null || crc != IO.crc(a) ? download(dst, archive, group) : CompletableFuture.completedFuture(null));
    }

    default CompletableFuture<Integer> getArchiveCount() {
       return getMasterIndex().thenApply(mi -> mi.indices.length);
    }

    default CompletableFuture<MasterIndex> getMasterIndex() {
        return getArchiveCount().thenCompose(n -> {
            var fs = new CompletableFuture[n];
            for (var i = 0; i < n; i++) {
                fs[i] = buildMasterIndexIndex(i);
            }
            return CompletableFuture.allOf(fs).thenApply(__ -> {
                var miis = new MasterIndex.Index[n];
                for (var i = 0; i < n; i++) {
                    miis[i] = (MasterIndex.Index) fs[i].join();
                }
                return new MasterIndex(miis);
            });
        });
    }

    private CompletableFuture<MasterIndex.Index> buildMasterIndexIndex(int archive) {
        return getGroupCompressed(MASTER_ARCHIVE, archive).thenApply(gc -> {
            if (gc == null) return null;
            var crc = IO.crc(gc.duplicate());
            var version = Index.read(Compressor.decompress(gc)).version;
            return new MasterIndex.Index(crc, version);
        });
    }

    default CompletableFuture<Index> getIndex(int archive) {
        return getGroup(MASTER_ARCHIVE, archive).thenApply(a -> a == null ? null : Index.read(a));
    }

    private CompletableFuture<Void> download(WritableCache dst, int archive, int group) {
        return getGroupCompressed(archive, group).thenCompose(a -> dst.setGroupCompressed(archive, group, a));
    }
}
