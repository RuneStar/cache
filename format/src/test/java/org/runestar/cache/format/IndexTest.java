package org.runestar.cache.format;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.runestar.cache.format.Rand.R;

public final class IndexTest {

    @Test public void test() {
        check(new Index(0, new Index.Group[0]));
        check(new Index(0, new Index.Group[]{
                        new Index.Group(0, 0, 0, 0, new Index.File[]{
                                new Index.File(0, 0)
                        })
        }));
        check(new Index(2, new Index.Group[]{
                new Index.Group(0, 1, 0, 8, new Index.File[]{
                        new Index.File(1, 6),
                        new Index.File(2, 6)
                }),
                new Index.Group(1, 2, 0, 7, new Index.File[]{
                        new Index.File(0, 4)
                })
        }));
    }

    private static void check(Index index) {
        var encoded = index.encode();
        var decoded = Index.decode(encoded);
        assertEquals(index, decoded);
    }

    @Test public void testSplit() {
        for (int i = 0; i < 20; i++) {
            checkSplit(Rand.byteBuffers(R.nextInt(100), 500));
        }
    }

    private static void checkSplit(ByteBuffer[] files) {
        var merged = Index.Group.merge(files);
        var split = Index.Group.split(merged, files.length);
        assertArrayEquals(files, split);
    }
}
