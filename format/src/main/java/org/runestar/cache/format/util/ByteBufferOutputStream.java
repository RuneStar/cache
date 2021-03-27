package org.runestar.cache.format.util;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public final class ByteBufferOutputStream extends OutputStream {

    public ByteBuffer buf;

    public ByteBufferOutputStream(ByteBuffer buf) {
        this.buf = Objects.requireNonNull(buf);
    }

    public ByteBufferOutputStream(int initialCapacity) {
        buf = ByteBuffer.allocate(initialCapacity);
    }

    public ByteBufferOutputStream() {
        this(32);
    }

    private ByteBuffer reserve(int n) {
        if (buf.remaining() < n) {
            int cap = Math.max(buf.capacity() * 2, buf.position() + n);
            buf = ByteBuffer.allocate(cap).put(buf.flip());
        }
        return buf;
    }

    @Override public void write(byte[] b, int off, int len) {
        reserve(len).put(b, off, len);
    }

    @Override public void write(byte[] b) {
       write(b, 0, b.length);
    }

    @Override public void write(int b) {
        write((byte) b);
    }

    public void write(byte b) {
        reserve(Byte.BYTES).put(b);
    }

    public void writeShort(short s) {
        reserve(Short.BYTES).putShort(s);
    }

    public void writeInt(int n) {
        reserve(Integer.BYTES).putInt(n);
    }

    public void write(ByteBuffer buf) {
        reserve(buf.remaining()).put(buf);
    }

    @Override public String toString() {
        return "ByteBufferOutputStream(buf=" + buf + ')';
    }
}
