package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.util.Arrays;

public final class MasterIndex {

    public final Index[] indices;

    public MasterIndex(Index[] indices) {
        this.indices = indices;
    }

    public static MasterIndex read(ByteBuffer buf) {
        var count = buf.remaining() / Index.LENGTH;
        var is = new Index[count];
        for (var i = 0; i < count; i++) {
            is[i] = new Index(buf.getInt(), buf.getInt());
        }
        return new MasterIndex(is);
    }

    @Override
    public String toString() {
        return "MasterIndex(indices=" + Arrays.toString(indices) + ')';
    }

    public static final class Index {

        private static final int LENGTH = Integer.BYTES * 2;

        public final int crc;

        public final int version;

        public Index(int crc, int version) {
            this.crc = crc;
            this.version = version;
        }

        @Override
        public String toString() {
            return "Index(crc=" + crc + ", version=" + version + ')';
        }
    }
}