package org.runestar.cache.content.font;

import org.runestar.cache.content.CacheType;
import org.runestar.cache.content.io.Input;

public final class FontMetrics extends CacheType {

    public static final int ARCHIVE = 13;

    public final int[] advances = new int[256];

    public int ascent = 0;

    @Override public void decode(Input in) {
        for (int i = 0; i < 256; ++i) {
            advances[i] = in.g1();
        }
        ascent = in.g1();
    }
}
