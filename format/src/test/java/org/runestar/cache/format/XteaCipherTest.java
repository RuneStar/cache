package org.runestar.cache.format;

import org.junit.jupiter.api.Test;
import org.runestar.cache.format.util.IO;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class XteaCipherTest {

    @Test public void test() {
        for (var b : Rand.byteBuffers()) {
            check(b, Rand.xteaKey());
            checkEmptyKey(b);
        }
    }

    private static void check(ByteBuffer b, int[] key) {
        var copy = IO.getBuffer(b.duplicate());
        XteaCipher.encrypt(b, key);
        XteaCipher.decrypt(b, key);
        assertEquals(copy, b);
    }

    private static void checkEmptyKey(ByteBuffer b) {
        var key = new int[XteaCipher.KEY_SIZE];
        var copy = IO.getBuffer(b.duplicate());
        XteaCipher.encrypt(b, key);
        assertEquals(copy, b);
        XteaCipher.decrypt(b, key);
        assertEquals(copy, b);
    }
}
