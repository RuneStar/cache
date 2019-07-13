package org.runestar.cache.format;

import java.nio.ByteBuffer;

public final class XteaCipher {

    private XteaCipher() {}

    public static final int KEY_SIZE = 4;

    private static final int PHI = 0x9E3779B9;

    private static final int ROUNDS = 32;

    public static void encrypt(ByteBuffer buf, int[] key) {
        if (isKeyEmpty(key)) return;
        for (int i = buf.position(); i <= buf.limit() - (Integer.BYTES * 2); i += Integer.BYTES * 2) {
            int v0 = buf.getInt(i);
            int v1 = buf.getInt(i + Integer.BYTES);
            int sum = 0;
            for (int r = 0; r < ROUNDS; r++) {
                v0 += (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + key[sum & 3]);
                sum += PHI;
                v1 += (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + key[(sum >>> 11) & 3]);
            }
            buf.putInt(i, v0);
            buf.putInt(i + Integer.BYTES, v1);
        }
    }

    public static void decrypt(ByteBuffer buf, int[] key) {
        if (isKeyEmpty(key)) return;
        for (int i = buf.position(); i <= buf.limit() - (Integer.BYTES * 2); i += Integer.BYTES * 2) {
            int v0 = buf.getInt(i);
            int v1 = buf.getInt(i + Integer.BYTES);
            int sum = PHI * ROUNDS;
            for (int r = 0; r < ROUNDS; r++) {
                v1 -= (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + key[(sum >>> 11) & 3]);
                sum -= PHI;
                v0 -= (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + key[sum & 3]);
            }
            buf.putInt(i, v0);
            buf.putInt(i + Integer.BYTES, v1);
        }
    }

    private static boolean isKeyEmpty(int[] key) {
        if (key.length != KEY_SIZE) throw new IllegalArgumentException();
        for (int i = 0; i < KEY_SIZE; i++) {
            if (key[i] != 0) return false;
        }
        return true;
    }
}
