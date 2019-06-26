package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

public final class AnimBase extends CacheType {

    public static final int ARCHIVE = 1;

    public int count;

    public int[] transformTypes;

    public int[][] labels;

    @Override public void decode(ByteBuffer buffer) {
        count = getUnsignedByte(buffer);
        transformTypes = new int[count];
        labels = new int[count][];
        for (int i = 0; i < count; i++) {
            transformTypes[i] = getUnsignedByte(buffer);
        }
        for (int j = 0; j < count; j++) {
            labels[j] = new int[getUnsignedByte(buffer)];
        }
        for (int k = 0; k < count; k++) {
            for (int l = 0; l < labels[k].length; l++) {
                labels[k][l] = getUnsignedByte(buffer);
            }
        }
    }
}
