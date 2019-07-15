package org.runestar.cache.format;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public final class ByteBufferOutputStream extends OutputStream {

    private final ByteBuffer buf;

    public ByteBufferOutputStream(ByteBuffer buf) {
        this.buf = Objects.requireNonNull(buf);
    }

    @Override public void write(byte[] b, int off, int len) {
        buf.put(b, off, len);
    }

    @Override public void write(int b) {
        buf.put((byte) b);
    }

    @Override public String toString() {
        return "ByteBufferOutputStream(buf=" + buf + ')';
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ByteBufferOutputStream)) return false;
        ByteBufferOutputStream other = (ByteBufferOutputStream) obj;
        return buf == other.buf;
    }

    @Override public int hashCode() {
        return System.identityHashCode(buf);
    }
}
