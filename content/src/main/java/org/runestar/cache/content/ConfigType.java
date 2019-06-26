package org.runestar.cache.content;

import java.nio.ByteBuffer;

public abstract class ConfigType extends CacheType {

    public static final int ARCHIVE = 2;

    @Override public final void decode(ByteBuffer buffer) {
        decode0(buffer);
        postDecode();
    }

    protected abstract void decode0(ByteBuffer buffer);

    protected void postDecode() {}
}
