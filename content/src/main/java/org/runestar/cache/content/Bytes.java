package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public final class Bytes {

    private Bytes() {}

    public static final Charset CHARSET = Charset.forName("windows-1252");

    public static String readString(ByteBuffer buffer) {
        int origPos = buffer.position();
        int length = 0;
        while (buffer.get() != 0) length++;
        if (length == 0) return "";
        var byteArray = new byte[length];
        buffer.position(origPos);
        buffer.get(byteArray);
        buffer.position(buffer.position() + 1);
        return new String(byteArray, CHARSET);
    }

    public static int getMedium(ByteBuffer buffer) {
        return (buffer.getShort() << 8) | (buffer.get() & 0xFF);
    }

    public static Map<Integer, Object> readParams(ByteBuffer buffer) {
        int length = Byte.toUnsignedInt(buffer.get());
        var params = new HashMap<Integer, Object>(length);
        for (int i = 0; i < length; i++) {
            boolean isString = buffer.get() != 0;
            int key = getMedium(buffer);
            Object value = isString ? readString(buffer) : buffer.getInt();
            params.put(key, value);
        }
        return params;
    }
}
