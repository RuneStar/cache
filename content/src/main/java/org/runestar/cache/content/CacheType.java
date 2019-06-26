package org.runestar.cache.content;

import java.nio.ByteBuffer;

public abstract class CacheType {

    public abstract void decode(ByteBuffer buffer);

    public final void decode(byte[] bytes) {
        decode(ByteBuffer.wrap(bytes));
    }
}