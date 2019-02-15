package org.runestar.cache.format;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public enum Compressor {

    BZIP2(1) {

        private final int BLOCK_SIZE = 1;

        private final byte[] HEADER = ("BZh" + BLOCK_SIZE).getBytes(StandardCharsets.US_ASCII);

        @Override
        protected void compress0(ByteBuffer buf, ByteBufferOutputStream dst) {
            var decompressedSize = buf.remaining();
            var startPos = dst.buf.position();
            try (var in = new ByteBufferInputStream(buf);
                 var out = new BZip2CompressorOutputStream(dst, BLOCK_SIZE)) {
                in.transferTo(out);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            dst.buf.putInt(startPos, decompressedSize);
        }

        @Override
        protected ByteBuffer decompress0(ByteBuffer buf) {
            var output = new byte[buf.getInt()];
            try (var seq = new SequenceInputStream(new ByteArrayInputStream(HEADER), new ByteBufferInputStream(buf));
                 var in = new BZip2CompressorInputStream(seq)) {
                IO.readBytes(in, output);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            return ByteBuffer.wrap(output);
        }
    },

    GZIP(2) {

        @Override
        protected void compress0(ByteBuffer buf, ByteBufferOutputStream dst) {
            dst.ensureRemaining(Integer.BYTES).putInt(buf.remaining());
            try (var in = new ByteBufferInputStream(buf);
                 var out = new GZIPOutputStream(dst)) {
                in.transferTo(out);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        protected ByteBuffer decompress0(ByteBuffer buf) {
            var output = new byte[buf.getInt()];
            try (var in = new GZIPInputStream(new ByteBufferInputStream(buf))) {
                IO.readBytes(in, output);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            return ByteBuffer.wrap(output);
        }
    };

    private final byte id;

    Compressor(int id) {
        this.id = (byte) id;
    }

    abstract protected void compress0(ByteBuffer buf, ByteBufferOutputStream dst);

    abstract protected ByteBuffer decompress0(ByteBuffer buf);

    public static int headerLength(byte id) {
        switch (id) {
            case 0: return 0;
            case 1: case 2: return Integer.BYTES;
        }
        throw new IllegalArgumentException(Byte.toString(id));
    }

    private static Compressor of(byte id) {
        switch (id) {
            case 1: return BZIP2;
            case 2: return GZIP;
        }
        throw new IllegalArgumentException(Byte.toString(id));
    }

    public static ByteBuffer decompress(ByteBuffer buf) {
        return decompress(buf, null);
    }

    public static ByteBuffer decompress(ByteBuffer buf, int[] key) {
        var compressor = buf.get();
        var compressedLimit = buf.getInt() + buf.position() + headerLength(compressor);
        var totalLimit = buf.limit();
        buf.limit(compressedLimit);
        if (key != null) {
            XteaCipher.decrypt(buf, key);
        }
        var decompressed = compressor == 0 ? IO.getSlice(buf) : of(compressor).decompress0(buf);
        buf.limit(totalLimit);
        return decompressed;
    }
}
