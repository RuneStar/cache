package org.runestar.cache.format;

import java.nio.ByteBuffer;

public final class IndexMaster {

    public final int crc;

    public final int version;

    public final ByteBuffer groupCompressed;

    public final Index index;

    public IndexMaster(int crc, int version) {
        this(crc, version, null, null);
    }

    private IndexMaster(int crc, int version, ByteBuffer groupCompressed, Index index) {
        this.crc = crc;
        this.version = version;
        this.groupCompressed = groupCompressed;
        this.index = index;
    }

    public static IndexMaster[] decodeAll(ByteBuffer masterIndex) {
        var count = masterIndex.remaining() / (Integer.BYTES * 2);
        var mi = new IndexMaster[count];
        for (var i = 0; i < count; i++) {
            mi[i] = new IndexMaster(masterIndex.getInt(), masterIndex.getInt());
        }
        return mi;
    }

    public static IndexMaster decode(ByteBuffer groupCompressed) {
        var crc = IO.crc(groupCompressed.duplicate());
        var index = Index.decode(Compressor.decompress(groupCompressed.duplicate()));
        return new IndexMaster(crc, index.version, groupCompressed, index);
    }

    @Override public String toString() {
        return "IndexMaster(crc=" + crc + ", version=" + version + ')';
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IndexMaster)) return false;
        IndexMaster other = (IndexMaster) obj;
        if (crc != other.crc) return false;
        return version == other.version;
    }

    @Override public int hashCode() {
        return crc ^ version;
    }
}
