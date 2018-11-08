package org.runestar.cache;

import java.nio.ByteBuffer;

public final class Archive {

    private Archive() {}

    public static ByteBuffer[] split(ByteBuffer buf, int fileCount) {
        var files = new ByteBuffer[fileCount];
        if (fileCount == 1) {
            files[0] = buf;
            return files;
        }
        if (buf.get(buf.limit() - 1) != 1) throw new IllegalStateException();
        var fileSizes = buf.duplicate().position(buf.limit() - 1 - fileCount * Integer.BYTES);
        var fileSize = 0;
        for (var fi = 0; fi < fileCount; fi++) {
            fileSize += fileSizes.getInt();
            files[fi] = IO.getSlice(buf, fileSize);
        }
        buf.position(buf.limit());
        return files;
    }
}
