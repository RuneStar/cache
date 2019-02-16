package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public interface WritableStore extends ReadableStore {

    CompletableFuture<Void> setArchiveCompressed(int index, int archive, ByteBuffer buf);
}
