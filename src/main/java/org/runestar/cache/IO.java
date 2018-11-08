package org.runestar.cache;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.zip.CRC32;

public final class IO {

    private IO() {}

    public static void readNBytes(InputStream in, byte[] dst) throws IOException {
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

    public static void writeInt(OutputStream out, int n) throws IOException {
        out.write(n >>> 24);
        out.write(n >>> 16);
        out.write(n >>> 8);
        out.write(n);
    }

    public static byte[] getArray(ByteBuffer buf) {
        return getArray(buf, buf.remaining());
    }

    public static byte[] getArray(ByteBuffer buf, int len) {
        var b = new byte[len];
        buf.get(b);
        return b;
    }

    public static int[] getArray(IntBuffer buf) {
        return getArray(buf, buf.remaining());
    }

    public static int[] getArray(IntBuffer buf, int len) {
        var b = new int[len];
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
        return (buf.getShort() << 8) | buf.get();
    }

    public static ByteBuffer putMedium(ByteBuffer buf, int value) {
        return buf.putShort((short) (value >>> 8)).put((byte) value);
    }

    public static byte[] content(ByteBuffer buf) {
        var b = getArray(buf);
        buf.position(buf.position() - b.length);
        return b;
    }
}
