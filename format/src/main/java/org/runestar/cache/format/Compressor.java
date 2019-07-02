package org.runestar.cache.format;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;

public enum Compressor {

    NONE(0, 0) {

        @Override protected ByteBuffer decompress0(ByteBuffer buf) {
            return IO.getSlice(buf);
        }
    },

    BZIP2(1, Integer.BYTES) {

        private static final int BLOCK_SIZE = 1;

        private final byte[] HEADER = {'B', 'Z', 'h', '0' + BLOCK_SIZE};

        @Override protected ByteBuffer decompress0(ByteBuffer buf) {
            var output = new byte[buf.getInt()];
            try (var in = new BZip2CompressorInputStream(new SequenceInputStream(new ByteArrayInputStream(HEADER), new ByteBufferInputStream(buf)))) {
                IO.readBytes(in, output);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            return ByteBuffer.wrap(output);
        }
    },

    GZIP(2, Integer.BYTES) {

        private final ByteBuffer HEADER = ByteBuffer.wrap(new byte[]{31, -117, Deflater.DEFLATED, 0, 0, 0, 0, 0, 0, 0});

        @Override protected ByteBuffer decompress0(ByteBuffer buf) {
            var output = new byte[buf.getInt()];
            if (!IO.getSlice(buf, HEADER.limit()).equals(HEADER)) throw new IllegalArgumentException();
            IO.inflate(IO.getSlice(buf, buf.remaining() - Integer.BYTES * 2), output);
            if (Integer.reverseBytes(buf.getInt()) != IO.crc(output)) throw new IllegalArgumentException();
            if (Integer.reverseBytes(buf.getInt()) != output.length) throw new IllegalArgumentException();
            return ByteBuffer.wrap(output);
        }
    };

    public final byte id;

    public final int headerSize;

    Compressor(int id, int headerSize) {
        this.id = (byte) id;
        this.headerSize = headerSize;
    }

    abstract protected ByteBuffer decompress0(ByteBuffer buf);

    public static Compressor of(byte id) {
        switch (id) {
            case 0: return NONE;
            case 1: return BZIP2;
            case 2: return GZIP;
        }
        throw new IllegalArgumentException(Byte.toString(id));
    }

    public static ByteBuffer decompress(ByteBuffer buf) {
        return decompress(buf, null);
    }

    public static ByteBuffer decompress(ByteBuffer buf, int[] key) {
        var compressor = of(buf.get());
        var compressedLimit = buf.getInt() + buf.position() + compressor.headerSize;
        if (buf.limit() != compressedLimit) throw new IllegalArgumentException();
        if (key != null) XteaCipher.decrypt(buf, key);
        return compressor.decompress0(buf);
    }
}
