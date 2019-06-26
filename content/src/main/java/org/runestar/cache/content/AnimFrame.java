package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

public final class AnimFrame extends CacheType {

    public static final int ARCHIVE = 0;

    public final AnimBase base;

    public boolean hasAlphaTransform = false;

    public int transformCount = -1;

    public int[] labels = new int[500];

    public int[] xs = null;

    public int[] ys = null;

    public int[] zs = null;

    public AnimFrame(AnimBase base) {
        this.base = base;
    }

    @Override public void decode(ByteBuffer buffer) {
        int ub = getUnsignedByte(buffer);
        xs = new int[ub];
        ys = new int[ub];
        zs = new int[ub];
        var buffer2 = buffer.duplicate().position(buffer.position() + ub);
        transformCount = 0;
        int n = -1;
        for (int i = 0; i < ub; i++) {
            int ub2 = getUnsignedByte(buffer);
            if (ub2 > 0) {
                if (base.transformTypes[i] != 0) {
                    for (int j = i - 1; j > n; j--) {
                        if (base.transformTypes[j] == 0) {
                            labels[transformCount] = j;
                            xs[transformCount] = 0;
                            ys[transformCount] = 0;
                            zs[transformCount] = 0;
                            transformCount++;
                            break;
                        }
                    }
                }
                labels[transformCount] = i;
                int n2 = base.transformTypes[i] == 3 ? 128 : 0;
                xs[transformCount] = (ub2 & 1) != 0 ? getShortSmart(buffer2) : n2;
                ys[transformCount] = (ub2 & 2) != 0 ? getShortSmart(buffer2) : n2;
                zs[transformCount] = (ub2 & 4) != 0 ? getShortSmart(buffer2) : n2;
                n = i;
                transformCount++;
                hasAlphaTransform = hasAlphaTransform || base.transformTypes[i] == 5;
            }
        }
    }
}
