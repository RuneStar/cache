package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public interface WritableStore extends ReadableStore {

    CompletableFuture<Void> setArchive(int index, int archive, ByteBuffer buf);
}
