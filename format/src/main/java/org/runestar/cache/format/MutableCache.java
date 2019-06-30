package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public interface MutableCache extends Cache {

    CompletableFuture<Void> setGroupCompressed(int archive, int group, ByteBuffer buf);

    @Override CompletableFuture<Integer> getArchiveCount();

    @Override default CompletableFuture<MasterIndex> getMasterIndex() {
        return getArchiveCount().thenCompose(n -> {
            var fs = new CompletableFuture[n];
            for (int i = 0; i < n; i++) {
                fs[i] = buildMasterIndexIndex(i);
            }
            return CompletableFuture.allOf(fs).thenApply(o -> {
                var miis = new MasterIndex.Index[n];
                for (int i = 0; i < n; i++) {
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
            var version = Index.decode(Compressor.decompress(gc)).version;
            return new MasterIndex.Index(crc, version);
        });
    }
}
