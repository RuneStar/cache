package org.runestar.cache;

import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class ByteBufferOutputStream extends OutputStream {

    public ByteBuffer buf;

    public ByteBufferOutputStream(int initialSize) {
        buf = ByteBuffer.allocate(initialSize);
    }

    public ByteBuffer ensureRemaining(int len) {
        if (len > buf.remaining()) {
            var newSize = Math.max(buf.position() + len, buf.capacity() * 2);
            buf = ByteBuffer.allocate(newSize).put(buf.flip());
        }
        return buf;
    }

    @Override
    public void write(int b) {
        ensureRemaining(1).put((byte) b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        ensureRemaining(len).put(b, off, len);
    }
}
