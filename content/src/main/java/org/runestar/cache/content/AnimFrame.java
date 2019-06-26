package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

public final class AnimFrame extends CacheType {

    public static final int ARCHIVE = 0;

    public final AnimBase base;

    public boolean transparency = false;

    public int transformCount = -1;

    public int[] transforms = null;

    public int[] xs = null;

    public int[] ys = null;

    public int[] zs = null;

    public AnimFrame(AnimBase base) {
        this.base = base;
    }

    @Override public void decode(ByteBuffer buffer) {
        int maxTransform = getUnsignedByte(buffer);
        transforms = new int[maxTransform];
        xs = new int[maxTransform];
        ys = new int[maxTransform];
        zs = new int[maxTransform];
        var params = buffer.duplicate().position(buffer.position() + maxTransform);
        transformCount = 0;
        int lastTransform = -1;
        for (int transform = 0; transform < maxTransform; transform++) {
            int paramMask = getUnsignedByte(buffer);
            if (paramMask == 0) continue;
            if (base.transformTypes[transform] != AnimBase.TRANSFORM_ORIGIN) {
                for (int t = transform - 1; t > lastTransform; t--) {
                    if (base.transformTypes[t] == AnimBase.TRANSFORM_ORIGIN) {
                        transforms[transformCount] = t;
                        xs[transformCount] = 0;
                        ys[transformCount] = 0;
                        zs[transformCount] = 0;
                        transformCount++;
                        break;
                    }
                }
            }
            transforms[transformCount] = transform;
            int paramDefault = base.transformTypes[transform] == AnimBase.TRANSFORM_SCALE ? 128 : 0;
            xs[transformCount] = (paramMask & 0b001) != 0 ? getShortSmart(params) : paramDefault;
            ys[transformCount] = (paramMask & 0b010) != 0 ? getShortSmart(params) : paramDefault;
            zs[transformCount] = (paramMask & 0b100) != 0 ? getShortSmart(params) : paramDefault;
            lastTransform = transform;
            transformCount++;
            transparency = transparency || base.transformTypes[transform] == AnimBase.TRANSFORM_TRANSPARENCY;
        }
    }
}
