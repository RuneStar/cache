package org.runestar.cache.format;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;

public enum Compressor {

    NONE(0, 0) {

        @Override protected ByteBuffer decompress0(ByteBuffer buf) {
            return IO.getBuffer(buf);
        }

        @Override protected void compress0(ByteBuffer buf, ByteBuffer dst) {
            dst.put(buf);
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

        @Override protected void compress0(ByteBuffer buf, ByteBuffer dst) {
            int start = dst.position();
            int len = buf.remaining();
            try (var out = new BZip2CompressorOutputStream(new ByteBufferOutputStream(dst), BLOCK_SIZE)) {
                new ByteBufferInputStream(buf).transferTo(out);
            } catch (IOException e) {
                throw new BufferOverflowException();
            }
            dst.putInt(start, len);
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

        @Override protected void compress0(ByteBuffer buf, ByteBuffer dst) {
            int len = buf.remaining();
            dst.putInt(len);
            dst.put(HEADER.duplicate());
            IO.deflate(buf.duplicate(), dst);
            dst.putInt(Integer.reverseBytes(IO.crc(buf)));
            dst.putInt(Integer.reverseBytes(len));
        }
    };

    private final byte id;

    public final int headerSize;

    Compressor(int id, int headerSize) {
        this.id = (byte) id;
        this.headerSize = headerSize;
    }

    abstract protected ByteBuffer decompress0(ByteBuffer buf);

    abstract protected void compress0(ByteBuffer buf, ByteBuffer dst);

    private ByteBuffer tryCompress(ByteBuffer buf, int maxSize) {
        var dst = ByteBuffer.allocate(maxSize);
        dst.position(1 + Integer.BYTES);
        try {
            compress0(buf, dst);
        } catch (BufferOverflowException e) {
            return null;
        }
        int n = dst.position() - 1 - Integer.BYTES - headerSize;
        return dst.flip().put(id).putInt(n).rewind();
    }

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
        if (buf.getInt() + compressor.headerSize != buf.remaining()) throw new IllegalArgumentException();
        if (key != null) XteaCipher.decrypt(buf = IO.getBuffer(buf), key);
        return compressor.decompress0(buf);
    }

    public static ByteBuffer compress(ByteBuffer buf) {
        return compress(buf, null);
    }

    public static ByteBuffer compress(ByteBuffer buf, int[] key) {
        if (key != null) XteaCipher.encrypt(buf = IO.getBuffer(buf), key);
        buf = IO.getSlice(buf);
        int size = 1 + Integer.BYTES + buf.remaining();
        var gzip = GZIP.tryCompress(buf.duplicate(), size - 1);
        if (gzip != null) size = gzip.remaining();
        var bzip = BZIP2.tryCompress(buf.duplicate(), size - 1);
        if (bzip != null) return bzip;
        if (gzip != null) return gzip;
        return NONE.tryCompress(buf, size);
    }
}
