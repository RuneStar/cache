package org.runestar.cache.content;

import org.runestar.cache.content.io.Input;
import org.runestar.cache.content.io.Packet;

import java.nio.ByteBuffer;

public abstract class CacheType {

    public abstract void decode(Input in);

    public final void decode(ByteBuffer buffer) {
        decode(new Packet(buffer));
    }

    public final void decode(byte[] bytes) {
        decode(ByteBuffer.wrap(bytes));
    }
}