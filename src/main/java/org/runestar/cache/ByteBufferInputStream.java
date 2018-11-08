package org.runestar.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class ByteBufferInputStream extends InputStream {

    private final ByteBuffer buf;

    public ByteBufferInputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    @Override
    public int available() {
        return buf.remaining();
    }

    @Override
    public int read() {
        if (buf.hasRemaining()) {
            return Byte.toUnsignedInt(buf.get());
        } else {
            return -1;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) {
        var remaining = buf.remaining();
        if (remaining == 0 && len > 0) return -1;
        var n = Math.min(remaining, len);
        buf.get(b, off, n);
        return n;
    }

    @Override
    public byte[] readNBytes(int len) {
        return IO.getArray(buf, Math.min(buf.remaining(), len));
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) {
        var n = Math.min(buf.remaining(), len);
        buf.get(b, off, n);
        return n;
    }

    @Override
    public long skip(long n) {
        var count = (int) Math.min((long) buf.remaining(), n);
        buf.position(buf.position() + count);
        return count;
    }

    @Override
    public void mark(int readlimit) {
        buf.mark();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void reset() {
        buf.reset();
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        var len = buf.remaining();
        if (buf.hasArray()) {
            out.write(buf.array(), buf.arrayOffset() + buf.position(), len);
            buf.position(buf.limit());
        } else {
            out.write(IO.getArray(buf, len));
        }
        return len;
    }
}
