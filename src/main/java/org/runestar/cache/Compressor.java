package org.runestar.cache;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public enum Compressor {

    NONE((byte) 0, 0),

    BZIP2((byte) 1, Integer.BYTES) {

        private final int BLOCK_SIZE = 1;

        private final byte[] HEADER = ("BZh" + BLOCK_SIZE).getBytes(StandardCharsets.US_ASCII);

        @Override
        protected ByteBuffer compress0(ByteBuffer buf) {
            var decompressedSize = buf.getInt();
            var output = new ByteArrayOutputStream();
            try (var in = new ByteBufferInputStream(buf);
                 var out = new BZip2CompressorOutputStream(output, BLOCK_SIZE)) {
                in.transferTo(out);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            var outputArray = output.toByteArray();
            if (!Arrays.equals(outputArray, 0, HEADER.length, HEADER, 0, HEADER.length)) {
                throw new IllegalStateException();
            }
            return ByteBuffer.wrap(outputArray).putInt(0, decompressedSize);
        }

        @Override
        protected ByteBuffer decompress0(ByteBuffer buf) {
            var output = new byte[buf.getInt()];
            try (var seq = new SequenceInputStream(new ByteArrayInputStream(HEADER), new ByteBufferInputStream(buf));
                 var in = new BZip2CompressorInputStream(seq)) {
                IO.readNBytes(in, output);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            return ByteBuffer.wrap(output);
        }
    },

    GZIP((byte) 2, Integer.BYTES) {

        @Override
        protected ByteBuffer compress0(ByteBuffer buf) {
            var output = new ByteArrayOutputStream();
            try {
                IO.writeInt(output, buf.remaining());
            } catch (IOException e) {
                throw new IllegalStateException();
            }
            try (var in = new ByteBufferInputStream(buf);
                 var out = new GZIPOutputStream(output)) {
                in.transferTo(out);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            return ByteBuffer.wrap(output.toByteArray());
        }

        @Override
        protected ByteBuffer decompress0(ByteBuffer buf) {
            var output = new byte[buf.getInt()];
            try (var in = new GZIPInputStream(new ByteBufferInputStream(buf))) {
                IO.readNBytes(in, output);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            return ByteBuffer.wrap(output);
        }
    };

    public final byte id;

    public final int headerLength;

    Compressor(byte id, int headerLength) {
        this.id = id;
        this.headerLength = headerLength;
    }

    protected ByteBuffer compress0(ByteBuffer buf) {
        return buf;
    }

    protected ByteBuffer decompress0(ByteBuffer buf) {
        return buf;
    }

    public static Compressor of(byte id) {
        switch (id) {
            case 0: return NONE;
            case 1: return BZIP2;
            case 2: return GZIP;
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    public static ByteBuffer decompress(ByteBuffer buf) {
        return decompress(buf, null);
    }

    public static ByteBuffer decompress(ByteBuffer buf, int[] key) {
        var compressor = Compressor.of(buf.get());
        var compressedLength = buf.getInt() + compressor.headerLength;
        var compressedLimit = buf.position() + compressedLength;
        var totalLimit = buf.limit();
        buf.limit(compressedLimit);
        if (key != null) {
            XteaCipher.decrypt(buf, key);
        }
        var decompressed = compressor.decompress0(buf);
        buf.limit(totalLimit);
        return decompressed;
    }
}
