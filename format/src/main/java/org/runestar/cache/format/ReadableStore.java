package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface ReadableStore {

    int META_INDEX = 255;

    CompletableFuture<ByteBuffer> getArchiveCompressed(int index, int archive);

    default CompletableFuture<ByteBuffer> getArchive(int index, int archive) {
        return getArchive(index, archive, null);
    }

    default CompletableFuture<ByteBuffer> getArchive(int index, int archive, int[] key) {
        return getArchiveCompressed(index, archive).thenApply(a -> a == null ? null : Compressor.decompress(a, key));
    }

    default CompletableFuture<Void> update(WritableStore dst) {
        return getIndexVersions()
                .thenCombine(dst.getIndexVersions(), (sivs, divs) -> {
                    var fs = new ArrayList<CompletableFuture<Void>>();
                    for (var i = 0; i < sivs.length; i++) {
                        var siv = sivs[i];
                        var div = i < divs.length ? divs[i] : null;
                        if (div == null || siv.crc != div.crc || siv.version != div.version) {
                            fs.add(update(dst, i));
                        }
                    }
                    return CompletableFuture.allOf(fs.toArray(new CompletableFuture[0]));
                })
                .thenCompose(Function.identity());
    }

    default CompletableFuture<Void> update(WritableStore dst, int index) {
        return getArchiveCompressed(META_INDEX, index)
                .thenCombine(dst.getArchiveCompressed(META_INDEX, index), (sa, da) ->  {
                    var sias = IndexAttributes.read(Compressor.decompress(sa.duplicate()));
                    var dias = da == null ? null : IndexAttributes.read(Compressor.decompress(da.duplicate()));
                    var fs = new ArrayList<CompletableFuture<Void>>(sias.archives.length + 1);
                    for (var a = 0; a < sias.archives.length; a++) {
                        var sia = sias.archives[a];
                        var dia = (da == null || a >= dias.archives.length) ? null : dias.archives[a];
                        if (dia == null || sia.version != dia.version || sia.crc != dia.crc) {
                            fs.add(download(dst, index, sia.id));
                        } else {
                            fs.add(update(dst, index, sia.id, sia.crc));
                        }
                    }
                    if (!sa.equals(da)) {
                        fs.add(dst.setArchiveCompressed(META_INDEX, index, sa));
                    }
                    return CompletableFuture.allOf(fs.toArray(new CompletableFuture[0]));
                })
                .thenCompose(Function.identity());
    }

    private CompletableFuture<Void> update(WritableStore dst, int index, int archive, int crc) {
        return dst.getArchiveCompressed(index, archive)
                .thenCompose(a -> a == null || crc != IO.crc(a) ? download(dst, index, archive) : CompletableFuture.completedFuture(null));
    }

    default CompletableFuture<Integer> getIndexCount() {
       return getIndexVersions().thenApply(ivs -> ivs.length);
    }

    default CompletableFuture<IndexVersion[]> getIndexVersions() {
        return getIndexCount().thenCompose(n -> {
            var fs = new CompletableFuture[n];
            for (var i = 0; i < n; i++) {
                fs[i] = buildIndexVersion(i);
            }
            return CompletableFuture.allOf(fs).thenApply(__ -> {
                var ivs = new IndexVersion[n];
                for (var i = 0; i < n; i++) {
                    ivs[i] = (IndexVersion) fs[i].join();
                }
                return ivs;
            });
        });
    }

    private CompletableFuture<IndexVersion> buildIndexVersion(int index) {
        return getArchiveCompressed(META_INDEX, index).thenApply(a -> {
            if (a == null) return null;
            var crc = IO.crc(a.duplicate());
            var version = IndexAttributes.read(Compressor.decompress(a)).version;
            return new IndexVersion(crc, version);
        });
    }

    default CompletableFuture<IndexAttributes> getIndexAttributes(int index) {
        return getArchive(META_INDEX, index).thenApply(a -> a == null ? null : IndexAttributes.read(a));
    }

    private CompletableFuture<Void> download(WritableStore dst, int index, int archive) {
        return getArchiveCompressed(index, archive).thenCompose(a -> dst.setArchiveCompressed(index, archive, a));
    }
}
