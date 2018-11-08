package org.runestar.cache;

import java.nio.ByteBuffer;

public final class XteaCipher {

    private XteaCipher() {}

    private static final int KEY_SIZE = 4;

    private static final int PHI = -1640531527;

    private static final int ROUNDS = 32;

    public static void encrypt(ByteBuffer buffer, int[] key) {
        if (isKeyEmpty(key)) return;
        for (var i = buffer.position(); i <= buffer.limit() - 8; i += 8) {
            var v0 = buffer.getInt(i);
            var v1 = buffer.getInt(i + 4);
            var sum = 0;
            for (var r = 0; r <= ROUNDS; r++) {
                v0 += (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + key[sum & 3]);
                sum += PHI;
                v1 += (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + key[(sum >>> 11) & 3]);
            }
            buffer.putInt(i, v0);
            buffer.putInt(i + 4, v1);
        }
    }

    public static void decrypt(ByteBuffer buffer, int[] key) {
        if (isKeyEmpty(key)) return;
        for (var i = buffer.position(); i <= buffer.limit() - 8; i += 8) {
            var v0 = buffer.getInt(i);
            var v1 = buffer.getInt(i + 4);
            var sum = PHI * ROUNDS;
            for (var r = 0; r <= ROUNDS; r++) {
                v1 -= (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + key[(sum >>> 11) & 3]);
                sum -= PHI;
                v0 -= (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + key[sum & 3]);
            }
            buffer.putInt(i, v0);
            buffer.putInt(i + 4, v1);
        }
    }

    private static boolean isKeyEmpty(int[] key) {
        if (key.length != KEY_SIZE) throw new IllegalArgumentException();
        return key[0] == 0 && key[1] == 0 && key[2] == 0 && key[3] == 0;
    }
}
