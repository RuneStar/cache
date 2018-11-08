package org.runestar.cache;

import java.nio.ByteBuffer;

public final class Archives {

    private Archives() {}

    public static ByteBuffer[] split(int[] fileIds, ByteBuffer buf) {
        var size = fileIds[fileIds.length - 1] + 1;
        var files = new ByteBuffer[size];
        if (size == 1) {
            files[0] = buf;
        } else {
            var chunks = buf.get(buf.limit() - 1);
            if (chunks != 1) throw new IllegalStateException();
            var fileSizes = buf.slice();
            fileSizes.position(buf.limit() - 1 - fileIds.length * Integer.BYTES);
            buf.mark();
            var fileSize = 0;
            for (int fileId : fileIds) {
                fileSize += buf.getInt();
                var file = buf.slice();
                file.limit(file.position() + fileSize);
                buf.position(buf.position() + fileSize);
                files[fileId] = file;
            }
            buf.reset();
        }
        return files;
    }
}
