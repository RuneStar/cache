package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public interface RemoteCache extends AutoCloseable {

    int MASTER_ARCHIVE = 255;

    CompletableFuture<ByteBuffer> getGroupCompressed(int archive, int group);

    default CompletableFuture<Group> getGroup(int archive, int group) {
        return getGroup(archive, group, null);
    }

    default CompletableFuture<Group> getGroup(int archive, int group, int[] key) {
        return getGroupCompressed(archive, group).thenApply(gc -> Group.decompress(gc, key));
    }

    default CompletableFuture<IndexMaster[]> getMasterIndex() {
        return getGroup(MASTER_ARCHIVE, MASTER_ARCHIVE).thenApply(g -> IndexMaster.decodeAll(g.data));
    }

    default CompletableFuture<Index> getIndex(int archive) {
        return getGroup(MASTER_ARCHIVE, archive).thenApply(g -> Index.decode(g.data));
    }
}
