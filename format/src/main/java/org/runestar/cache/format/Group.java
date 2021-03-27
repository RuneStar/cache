package org.runestar.cache.format;

import org.runestar.cache.format.util.ByteBufferOutputStream;
import org.runestar.cache.format.util.IO;

import java.nio.ByteBuffer;

public final class Group {

    public final Compressor compressor;

    public final ByteBuffer data;

    public final int crc32;

    public final int version;

    private Group(Compressor compressor, ByteBuffer data, int crc32, int version) {
        this.compressor = compressor;
        this.data = data;
        this.crc32 = crc32;
        this.version = version;
    }

    public static Group decompress(ByteBuffer groupCompressed, int[] key) {
        int start = groupCompressed.position();
        var compressor = Compressor.of(groupCompressed.get());
        int len = groupCompressed.getInt() + compressor.headerSize();
        var b = IO.getSlice(groupCompressed, len);
        int crc32 = IO.crc32(groupCompressed.duplicate().flip().position(start));
        if (key != null) XteaCipher.decrypt(b = IO.getBuffer(b), key);
        var data = compressor.decompress(b);
        int version = 0;
        if (groupCompressed.hasRemaining()) {
            version = Short.toUnsignedInt(groupCompressed.getShort());
            if (groupCompressed.hasRemaining()) throw new IllegalArgumentException();
        }
        return new Group(compressor, data, crc32, version);
    }

    public static ByteBuffer compress(Compressor compressor, ByteBuffer data, int version, int[] key) {
        var out = new ByteBufferOutputStream();
        out.write(compressor.id());
        int pos = out.buf.position();
        out.writeInt(0);
        compressor.compress(data, out);
        out.buf.putInt(pos, out.buf.position() - pos - compressor.headerSize() - 4);
        if (key != null) XteaCipher.encrypt(out.buf.duplicate().flip().position(5), key);
        if (version != 0) out.writeShort((short) version);
        return out.buf.flip();
    }

    public static ByteBuffer compress(Compressor compressor, ByteBuffer data, int[] key) {
        return compress(compressor, data, 0, key);
    }

    @Override public String toString() {
        return "Group(compressor=" + compressor + ", data=" + data + ", crc32=" + crc32 + ", version=" + version + ')';
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Group)) return false;
        Group other = (Group) obj;
        return crc32 == other.crc32
                && compressor == other.compressor
                && version == other.version
                && data.equals(other.data);
    }

    @Override public int hashCode() {
        return crc32;
    }
}
