package org.runestar.cache.format;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.zip.CRC32;

public final class IO {

    private IO() {}

    public static void readBytes(InputStream in, byte[] dst) throws IOException {
        readNBytes(in, dst, 0, dst.length);
    }

    public static void readNBytes(InputStream in, byte[] dst, int off, int len) throws IOException {
        if (in.readNBytes(dst, off, len) != len) throw new EOFException();
    }

    public static void closeQuietly(Throwable original, Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            original.addSuppressed(e);
        }
    }

    public static byte[] getArray(ByteBuffer buf, int len) {
        var b = new byte[len];
        buf.get(b);
        return b;
    }

    public static ByteBuffer getSlice(ByteBuffer buf) {
        return getSlice(buf, buf.remaining());
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

    public static int crc(ByteBuffer buf) {
        var crc = new CRC32();
        crc.update(buf);
        return (int) crc.getValue();
    }
}
