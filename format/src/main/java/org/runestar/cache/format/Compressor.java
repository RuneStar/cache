package org.runestar.cache.format;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.runestar.cache.format.util.ByteBufferInputStream;
import org.runestar.cache.format.util.ByteBufferOutputStream;
import org.runestar.cache.format.util.IO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public enum Compressor {

    NONE {

        @Override public ByteBuffer decompress(ByteBuffer compressed) {
            return IO.getBuffer(compressed);
        }

        @Override public void compress(ByteBuffer buf, ByteBufferOutputStream dst) {
            dst.write(buf);
        }
    },

    BZIP2 {

        private static final int BLOCK_SIZE = 1;

        private final byte[] HEADER = {'B', 'Z', 'h', '0' + BLOCK_SIZE};

        @Override public ByteBuffer decompress(ByteBuffer compressed) {
            var out = new byte[compressed.getInt()];
            try (var in = new BZip2CompressorInputStream(new SequenceInputStream(new ByteArrayInputStream(HEADER), new ByteBufferInputStream(compressed)))) {
                IO.readBytes(in, out);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return ByteBuffer.wrap(out);
        }

        @Override public void compress(ByteBuffer buf, ByteBufferOutputStream dst) {
            int start = dst.buf.position();
            int len = buf.remaining();
            try (var out = new BZip2CompressorOutputStream(dst, BLOCK_SIZE)) {
                IO.transferTo(buf, out);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            dst.buf.putInt(start, len);
        }
    },

    GZIP {

        @Override public ByteBuffer decompress(ByteBuffer compressed) {
            var out = new byte[compressed.getInt()];
            try (var in = new GZIPInputStream(new ByteBufferInputStream(compressed))) {
                IO.readBytes(in, out);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return ByteBuffer.wrap(out);
        }

        @Override public void compress(ByteBuffer buf, ByteBufferOutputStream dst) {
            dst.writeInt(buf.remaining());
            try (var out = new GZIPOutputStream(dst)) {
                IO.transferTo(buf, out);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    };

    abstract public ByteBuffer decompress(ByteBuffer compressed);

    abstract public void compress(ByteBuffer buf, ByteBufferOutputStream dst);

    public final ByteBuffer compress(ByteBuffer buf) {
        var out = new ByteBufferOutputStream();
        compress(buf, out);
        return out.buf.flip();
    }

    public byte id() {
        return (byte) ordinal();
    }

    public int headerSize() {
        return this == NONE ? 0 : Integer.BYTES;
    }

    public static Compressor of(byte id) {
        switch (id) {
            case 0: return NONE;
            case 1: return BZIP2;
            case 2: return GZIP;
        }
        throw new IllegalArgumentException("" + id);
    }

    public static Compressor best(ByteBuffer buf) {
        var out = new ByteBufferOutputStream();
        GZIP.compress(buf.duplicate(), out);
        int gzip = out.buf.position();
        out.buf.clear();
        BZIP2.compress(buf.duplicate(), out);
        int bzip2 = out.buf.position();
        int none = buf.remaining();
        if (none <= gzip && none <= bzip2) return NONE;
        if (gzip <= bzip2) return GZIP;
        return BZIP2;
    }
}
