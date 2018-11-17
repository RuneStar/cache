package org.runestar.cache.format;

import java.nio.ByteBuffer;

public final class IndexVersion {

    public static final int LENGTH = Integer.BYTES * 2;

    public final int crc;

    public final int version;

    public IndexVersion(int crc, int version) {
        this.crc = crc;
        this.version = version;
    }

    public static IndexVersion[] readAll(ByteBuffer buf) {
        var count = buf.remaining() / IndexVersion.LENGTH;
        var indexReferences = new IndexVersion[count];
        for (var i = 0; i < count; i++) {
            indexReferences[i] = new IndexVersion(buf.getInt(), buf.getInt());
        }
        return indexReferences;
    }

    @Override
    public String toString() {
        return "IndexVersion(crc=" + crc + ", version=" + version + ')';
    }
}