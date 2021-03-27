package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.util.SplittableRandom;

public final class Rand {

    private Rand() {}

    public static final SplittableRandom R = new SplittableRandom();

    public static ByteBuffer[] byteBuffers() {
        return byteBuffers(20, 1030);
    }

    public static ByteBuffer[] byteBuffers(int length, int capacityBound) {
        var a = new ByteBuffer[length];
        for (int i = 0; i < length; i++) {
            a[i] = byteBuffer(R.nextInt(capacityBound));
        }
        return a;
    }

    public static ByteBuffer byteBuffer(int capacity) {
        var array = new byte[capacity];
        if (capacity != 0) {
            array[0] = (byte) R.nextInt();
            for (int i = 1; i < array.length; i++) {
                array[i] = (byte) (array[i - 1] ^ R.nextInt(20));
            }
        }
        return ByteBuffer.wrap(array);
    }

    public static int[] xteaKey() {
        return new int[]{R.nextInt(), R.nextInt(), R.nextInt(), R.nextInt()};
    }
}
