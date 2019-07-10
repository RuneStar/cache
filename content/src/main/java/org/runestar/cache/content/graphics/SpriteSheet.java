package org.runestar.cache.content.graphics;

import org.runestar.cache.content.CacheType;
import org.runestar.cache.content.io.Input;

public final class SpriteSheet extends CacheType {

    public static final int ARCHIVE = 8;

    public Sprite[] sprites = null;

    public int width = 0;

    public int height = 0;

    public int[] palette = null;

    @Override public void decode(Input in) {
        int n = in.duplicate(in.remaining() - 2).g2();
        sprites = new Sprite[n];
        for (int i = 0; i < n; i++) {
            sprites[i] = new Sprite();
        }

        var in2 = in.duplicate(in.remaining() - 7 - n * 8);
        width = in2.g2();
        height = in2.g2();
        int paletteSize = in2.g1() + 1;
        for (var sprite : sprites) {
            sprite.xoffset = in2.g2();
        }
        for (var sprite : sprites) {
            sprite.yoffset = in2.g2();
        }
        for (var sprite : sprites) {
            sprite.subwidth = in2.g2();
        }
        for (var sprite : sprites) {
            sprite.subheight = in2.g2();
        }

        var in3 = in.duplicate(in.remaining() - 7 - n * 8 - 3 * (paletteSize - 1));
        palette = new int[paletteSize];
        for (int i = 0; i < paletteSize; i++) {
            int color = in3.g3();
            palette[i] = color == 0 ? 1 : color;
        }

        for (int i = 0; i < n; i++) {
            int sw = sprites[i].subwidth;
            int sh = sprites[i].subheight;
            int len = sw * sh;
            var px = new byte[len];
            sprites[i].pixels = px;
            int flags = in.g1();
            if (flags == 0) {
                for (int j = 0; j < len; j++) {
                    px[j] = in.g1s();
                }
            } else if (flags == 1) {
                for (int j = 0; j < sw; j++) {
                    for (int k = 0; k < sh; k++) {
                        px[j + k * sw] = in.g1s();
                    }
                }
            }
        }
    }
}
