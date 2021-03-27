package org.runestar.cache.format.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.zip.CRC32;

public final class IO {

    private IO() {}

    public static void readBytes(InputStream in, byte[] dst) throws IOException {
        readNBytes(in, dst, 0, dst.length);
    }

    public static void readNBytes(InputStream in, byte[] dst, int off, int len) throws IOException {
        if (in.readNBytes(dst, off, len) != len) throw new EOFException();
    }

    public static long transferTo(ByteBuffer buf, OutputStream out) throws IOException {
        int len = buf.remaining();
        if (buf.hasArray()) {
            out.write(buf.array(), buf.arrayOffset() + buf.position(), len);
            buf.position(buf.limit());
        } else {
            out.write(IO.getArray(buf, len));
        }
        return len;
    }

    public static void closeQuietly(Throwable original, AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            original.addSuppressed(e);
        }
    }

    public static ByteBuffer getBuffer(ByteBuffer buf) {
        return ByteBuffer.wrap(getArray(buf));
    }

    public static byte[] getArray(ByteBuffer buf) {
        return getArray(buf, buf.remaining());
    }

    public static byte[] getArray(ByteBuffer buf, int len) {
        var b = new byte[len];
        buf.get(b);
        return b;
    }

    public static ByteBuffer getSlice(ByteBuffer buf, int len) {
        var i = buf.position() + len;
        var slice = buf.duplicate().limit(i);
        buf.position(i);
        return slice;
    }

    public static ShortBuffer getShortSlice(ByteBuffer buf, int len) {
        return getSlice(buf, len * Short.BYTES).asShortBuffer();
    }

    public static IntBuffer getIntSlice(ByteBuffer buf, int len) {
        return getSlice(buf, len * Integer.BYTES).asIntBuffer();
    }

    public static int getMedium(ByteBuffer buf) {
        return (buf.getShort() << 8) | (buf.get() & 0xFF);
    }

    public static void putMedium(ByteBuffer buf, int value) {
        buf.putShort((short) (value >> 8)).put((byte) value);
    }

    public static int crc32(ByteBuffer buf) {
        var crc = new CRC32();
        crc.update(buf);
        return (int) crc.getValue();
    }

    public static ByteBuffer join(ByteBuffer... bufs) {
        if (bufs.length == 1) return bufs[0];
        int len = 0;
        for (var b : bufs) len += b.remaining();
        var buf = ByteBuffer.allocate(len);
        for (var b : bufs) buf.put(b);
        return buf.flip();
    }

    public static CompletableFuture<Void> allOf(Collection<? extends CompletableFuture<?>> cfs) {
        return CompletableFuture.allOf(cfs.toArray(new CompletableFuture[0]));
    }
}
