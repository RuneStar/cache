package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Buf {

    private Buf() {}

    private static final Charset CHARSET = Charset.forName("windows-1252");

    public static String getString(ByteBuffer buffer) {
        int start = buffer.position();
        while (buffer.get() != 0);
        int len = buffer.position() - 1 - start;
        if (len == 0) return "";
        byte[] array;
        int offset = 0;
        if (buffer.hasArray()) {
            array = buffer.array();
            offset = buffer.arrayOffset() + start;
        } else {
            array = new byte[len];
            buffer.position(start);
            buffer.get(array);
            buffer.position(buffer.position() + 1);
        }
        return new String(array, offset, len, CHARSET);
    }

    public static int getUnsignedByte(ByteBuffer buffer) {
        return Byte.toUnsignedInt(buffer.get());
    }

    public static int getUnsignedShort(ByteBuffer buffer) {
        return Short.toUnsignedInt(buffer.getShort());
    }

    public static int getUnsignedShortM1(ByteBuffer buffer) {
        int n = getUnsignedShort(buffer);
        if (n == 0xFFFF) n = -1;
        return n;
    }

    public static int getShortSmart(ByteBuffer buffer) {
        int b = getUnsignedByte(buffer);
        return b < 128 ? b - 64 : ((b << 8) | getUnsignedByte(buffer)) - 49152;
    }

    public static int getMedium(ByteBuffer buffer) {
        return (buffer.getShort() << 8) | (buffer.get() & 0xFF);
    }

    public static Map<Integer, Object> decodeParams(ByteBuffer buffer) {
        int length = getUnsignedByte(buffer);
        var params = new LinkedHashMap<Integer, Object>(length);
        for (int i = 0; i < length; i++) {
            boolean isString = buffer.get() != 0;
            params.put(getMedium(buffer), isString ? getString(buffer) : buffer.getInt());
        }
        return params;
    }
}
