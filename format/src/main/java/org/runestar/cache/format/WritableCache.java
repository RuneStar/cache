package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public interface WritableCache extends ReadableCache {

    CompletableFuture<Void> setGroupCompressed(int archive, int group, ByteBuffer buf);
}
