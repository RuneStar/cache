package org.runestar.cache.format

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import java.io.SequenceInputStream

enum class Compressor(val headerLength: Int) {

    NONE(0) {

        override fun compress(buffer: ByteBuf): ByteBuf {
            return buffer.retainedDuplicate()
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            return buffer.retainedDuplicate()
        }
    },

    BZIP2(Integer.BYTES) {

        private val BLOCK_SIZE = 1

        private val HEADER = "BZh$BLOCK_SIZE".toByteArray(Charsets.US_ASCII)

        override fun compress(buffer: ByteBuf): ByteBuf {
            buffer.markReaderIndex()
            val decompressedSize = buffer.readableBytes()
            val outputBuffer = Unpooled.buffer()
            buffer.inputStream().use { input ->
                BZip2CompressorOutputStream(outputBuffer.outputStream(), BLOCK_SIZE).use { output ->
                    input.copyTo(output)
                }
            }
            buffer.resetReaderIndex()
            val header = outputBuffer.toArray(0, HEADER.size)
            check(header.contentEquals(HEADER)) {
                "invalid header; expected ${HEADER.toString(Charsets.US_ASCII)} but got ${header.toString(Charsets.US_ASCII)}"
            }
            outputBuffer.setInt(0, decompressedSize) // replace bzip2 header with decompressedSize, both 4 bytes
            return outputBuffer
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            buffer.markReaderIndex()
            val expectedDecompressedSize = buffer.readInt()
            val outputBuffer = Unpooled.buffer(expectedDecompressedSize)
            BZip2CompressorInputStream(SequenceInputStream(HEADER.inputStream(), buffer.inputStream())).use { input ->
               outputBuffer.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            buffer.resetReaderIndex()
            val decompressedSize = outputBuffer.readableBytes()
            check(decompressedSize == expectedDecompressedSize) {
                "invalid decompressed size; expected $expectedDecompressedSize but got $decompressedSize"
            }
            return outputBuffer
        }
    },

    GZIP(Integer.BYTES) {

        override fun compress(buffer: ByteBuf): ByteBuf {
            buffer.markReaderIndex()
            val outputBuffer = Unpooled.buffer()
            outputBuffer.writeInt(buffer.readableBytes())
            buffer.inputStream().use { input ->
                GzipCompressorOutputStream(outputBuffer.outputStream()).use { output ->
                    input.copyTo(output)
                }
            }
            buffer.resetReaderIndex()
            return outputBuffer
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            buffer.markReaderIndex()
            val expectedDecompressedSize = buffer.readInt()
            val outputBuffer = Unpooled.buffer(expectedDecompressedSize)
            GzipCompressorInputStream(buffer.inputStream()).use { input ->
                outputBuffer.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            buffer.resetReaderIndex()
            val decompressedSize = outputBuffer.readableBytes()
            check(decompressedSize == expectedDecompressedSize) {
                "invalid decompressed size; expected $expectedDecompressedSize but got $decompressedSize"
            }
            return outputBuffer
        }
    };

    abstract fun decompress(buffer: ByteBuf): ByteBuf

    abstract fun compress(buffer: ByteBuf): ByteBuf

    companion object {

        @JvmField val VALUES = values().asList()

        fun of(id: Byte): Compressor = VALUES[id.toInt()]
    }
}