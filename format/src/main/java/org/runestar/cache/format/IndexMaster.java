package org.runestar.cache.format;

import java.nio.ByteBuffer;

public final class IndexMaster {

    public final int crc32;

    public final int version;

    private IndexMaster(int crc32, int version) {
        this.crc32 = crc32;
        this.version = version;
    }

    public static IndexMaster[] decodeAll(ByteBuffer masterIndex) {
        var count = masterIndex.remaining() / (Integer.BYTES * 2);
        var mi = new IndexMaster[count];
        for (var i = 0; i < count; i++) {
            mi[i] = new IndexMaster(masterIndex.getInt(), masterIndex.getInt());
        }
        return mi;
    }

    @Override public String toString() {
        return "IndexMaster(crc32=" + crc32 + ", version=" + version + ')';
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IndexMaster)) return false;
        IndexMaster other = (IndexMaster) obj;
        return crc32 == other.crc32 && version == other.version;
    }

    @Override public int hashCode() {
        return crc32;
    }
}
