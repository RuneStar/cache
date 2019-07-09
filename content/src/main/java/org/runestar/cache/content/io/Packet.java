package org.runestar.cache.content.io;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

public final class Packet implements Input {

    private static final Charset CHARSET = Charset.forName("windows-1252");

    private final ByteBuffer buf;

    public Packet(ByteBuffer buf) {
        this.buf = Objects.requireNonNull(buf);
    }

    @Override public Input duplicate(int offset) {
        return new Packet(buf.duplicate().position(buf.position() + offset));
    }

    @Override public int remaining() {
        return buf.remaining();
    }

    @Override public byte peek() {
        return buf.get(buf.position());
    }

    @Override public void skip(int n) {
        buf.position(buf.position() + n);
    }

    @Override public byte g1s() {
        return buf.get();
    }

    @Override public short g2s() {
        return buf.getShort();
    }

    @Override public int g4s() {
        return buf.getInt();
    }

    @Override public String gjstr() {
        int start = buf.position();
        while (buf.get() != 0);
        int len = buf.position() - 1 - start;
        if (len == 0) return "";
        byte[] array;
        int offset = 0;
        if (buf.hasArray()) {
            array = buf.array();
            offset = buf.arrayOffset() + start;
        } else {
            array = new byte[len];
            buf.position(start);
            buf.get(array);
            buf.position(buf.position() + 1);
        }
        return new String(array, offset, len, CHARSET);
    }

    @Override public String toString() {
        return "Packet(" + buf + ')';
    }

    @Override public int hashCode() {
        return System.identityHashCode(buf);
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Packet)) return false;
        Packet other = (Packet) obj;
        return buf == other.buf;
    }
}
