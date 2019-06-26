package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

public final class AnimBase extends CacheType {

    public static final int ARCHIVE = 1;

    public static final int TRANSFORM_ORIGIN = 0;

    public static final int TRANSFORM_TRANSLATE = 1;

    public static final int TRANSFORM_ROTATE = 2;

    public static final int TRANSFORM_SCALE = 3;

    public static final int TRANSFORM_TRANSPARENCY = 5;

    public int[] transformTypes;

    public int[][] transformLabels;

    @Override public void decode(ByteBuffer buffer) {
        int count = getUnsignedByte(buffer);
        transformTypes = new int[count];
        transformLabels = new int[count][];
        for (int i = 0; i < count; i++) {
            transformTypes[i] = getUnsignedByte(buffer);
        }
        for (int j = 0; j < count; j++) {
            transformLabels[j] = new int[getUnsignedByte(buffer)];
        }
        for (int k = 0; k < count; k++) {
            for (int l = 0; l < transformLabels[k].length; l++) {
                transformLabels[k][l] = getUnsignedByte(buffer);
            }
        }
    }
}
