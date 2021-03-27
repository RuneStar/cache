package org.runestar.cache.format;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CompressorTest {

    @Test public void test() {
        for (var b : Rand.byteBuffers()) {
            check(b);
        }
    }

    private static void check(ByteBuffer b) {
        check(Compressor.NONE, b);
        check(Compressor.BZIP2, b);
        check(Compressor.GZIP, b);
    }

    private static void check(Compressor compressor, ByteBuffer b) {
        var compressed = compressor.compress(b.duplicate());
        var decompressed = compressor.decompress(compressed);
        assertEquals(b, decompressed);
    }
}
