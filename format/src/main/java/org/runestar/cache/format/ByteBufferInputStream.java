package org.runestar.cache.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public final class ByteBufferInputStream extends InputStream {

    private final ByteBuffer buf;

    public ByteBufferInputStream(ByteBuffer buf) {
        this.buf = Objects.requireNonNull(buf);
    }

    @Override public int available() {
        return buf.remaining();
    }

    @Override public int read() {
        return buf.hasRemaining() ? Byte.toUnsignedInt(buf.get()) : -1;
    }

    @Override public int read(byte[] b, int off, int len) {
        if (len == 0) return 0;
        int remaining = buf.remaining();
        if (remaining == 0) return -1;
        int n = Math.min(remaining, len);
        buf.get(b, off, n);
        return n;
    }

    @Override public byte[] readNBytes(int len) {
        return IO.getArray(buf, Math.min(buf.remaining(), len));
    }

    @Override public int readNBytes(byte[] b, int off, int len) {
        int n = Math.min(buf.remaining(), len);
        buf.get(b, off, n);
        return n;
    }

    @Override public long skip(long n) {
        int count = (int) Math.min((long) buf.remaining(), n);
        buf.position(buf.position() + count);
        return count;
    }

    @Override public long transferTo(OutputStream out) throws IOException {
        int len = buf.remaining();
        if (buf.hasArray()) {
            out.write(buf.array(), buf.arrayOffset() + buf.position(), len);
            buf.position(buf.limit());
        } else {
            out.write(IO.getArray(buf, len));
        }
        return len;
    }

    @Override public String toString() {
        return "ByteBufferInputStream(buf=" + buf + ')';
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ByteBufferInputStream)) return false;
        ByteBufferInputStream other = (ByteBufferInputStream) obj;
        return buf == other.buf;
    }

    @Override public int hashCode() {
        return System.identityHashCode(buf);
    }
}
