package org.runestar.cache.content.config;

import org.runestar.cache.content.CacheType;
import org.runestar.cache.content.io.Input;

public abstract class ConfigType extends CacheType {

    public static final int ARCHIVE = 2;

    @Override public final void decode(Input in) {
        decode0(in);
        postDecode();
    }

    protected abstract void decode0(Input input);

    protected void postDecode() {}

    protected static void unrecognisedCode(int code) {
        throw new UnsupportedOperationException(Integer.toString(code));
    }
}
