package org.runestar.cache.content;

import java.nio.ByteBuffer;

public abstract class ConfigType {

    public static final int ARCHIVE = 2;

    public final void decode(ByteBuffer buffer) {
        decode0(buffer);
        postDecode();
    }

    protected abstract void decode0(ByteBuffer buffer);

    protected void postDecode() {}
}
