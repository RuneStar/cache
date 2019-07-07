package org.runestar.cache.content;

import org.runestar.cache.content.io.Input;
import org.runestar.cache.content.io.Packet;

public abstract class ConfigType extends CacheType {

    public static final int ARCHIVE = 2;

    public final void decode(Input input) {
        decode0(input);
        postDecode();
    }

    protected abstract void decode0(Input input);

    protected void postDecode() {}

    @Override public final void decode(Packet packet) {
        decode((Input) packet);
    }

    protected static void unrecognisedCode(int code) {
        throw new UnsupportedOperationException(Integer.toString(code));
    }
}
