package org.runestar.cache.format;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.runestar.cache.format.Rand.R;

public final class GroupTest {

    @Test public void test() {
        for (var b : Rand.byteBuffers()) {
            var compressor = Compressor.best(b);
            int version = R.nextInt(3);
            check(compressor, b, version, null);
            check(compressor, b, version, Rand.xteaKey());
        }
    }

    private static void check(Compressor compressor, ByteBuffer data, int version, int[] key) {
        var compressed = Group.compress(compressor, data.duplicate(), version, key);
        var decompressed = Group.decompress(compressed, key);
        assertEquals(compressor, decompressed.compressor);
        assertEquals(data, decompressed.data);
        assertEquals(version, decompressed.version);
    }
}
