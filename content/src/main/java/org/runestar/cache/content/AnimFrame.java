package org.runestar.cache.content;

import org.runestar.cache.content.io.Packet;

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

    @Override public void decode(Packet packet) {
        int maxTransform = packet.g1();
        transforms = new int[maxTransform];
        xs = new int[maxTransform];
        ys = new int[maxTransform];
        zs = new int[maxTransform];
        var params = packet.duplicate(maxTransform);
        transformCount = 0;
        int lastTransform = -1;
        for (int transform = 0; transform < maxTransform; transform++) {
            int paramMask = packet.g1();
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
            xs[transformCount] = (paramMask & 0b001) != 0 ? params.gSmart1or2s() : paramDefault;
            ys[transformCount] = (paramMask & 0b010) != 0 ? params.gSmart1or2s() : paramDefault;
            zs[transformCount] = (paramMask & 0b100) != 0 ? params.gSmart1or2s() : paramDefault;
            lastTransform = transform;
            transformCount++;
            transparency = transparency || base.transformTypes[transform] == AnimBase.TRANSFORM_TRANSPARENCY;
        }
    }
}
