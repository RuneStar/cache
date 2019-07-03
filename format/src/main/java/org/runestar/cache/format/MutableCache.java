package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public interface MutableCache extends Cache {

    CompletableFuture<Void> setGroupCompressed(int archive, int group, ByteBuffer buf);

    @Override CompletableFuture<Integer> getArchiveCount();

    @Override default CompletableFuture<IndexMaster[]> getMasterIndex() {
        return getArchiveCount().thenCompose(n -> {
            var fs = new CompletableFuture[n];
            for (int i = 0; i < n; i++) {
                fs[i] = getGroupCompressed(MASTER_ARCHIVE, i).thenApply(gc -> gc == null ? null : IndexMaster.decode(gc));
            }
            return CompletableFuture.allOf(fs).thenApply(o -> {
                var mi = new IndexMaster[n];
                for (int i = 0; i < n; i++) {
                    mi[i] = (IndexMaster) fs[i].join();
                }
                return mi;
            });
        });
    }
}
