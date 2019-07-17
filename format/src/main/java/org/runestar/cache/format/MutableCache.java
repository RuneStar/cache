package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public interface MutableCache extends Cache {

    CompletableFuture<Void> setGroupCompressed(int archive, int group, ByteBuffer buf);

    default CompletableFuture<Void> setGroup(int archive, int group, ByteBuffer buf, int[] key) {
        return setGroupCompressed(archive, group, Compressor.compress(buf, key));
    }

    default CompletableFuture<Void> setGroup(int archive, int group, ByteBuffer buf) {
        return setGroup(archive, group, buf, null);
    }

    default CompletableFuture<Void> setIndex(int archive, Index index) {
        return setGroup(MASTER_ARCHIVE, archive, index.encode());
    }

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
