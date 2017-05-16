package com.runesuite.cache.format

import com.runesuite.cache.extensions.getArray
import com.runesuite.cache.extensions.inputStream
import com.runesuite.cache.extensions.outputStream
import com.runesuite.cache.extensions.plus
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream

enum class Compressor(val id: Byte, val headerLength: Int) {

    NONE(0, 0) {
        override fun compress(buffer: ByteBuf): ByteBuf {
            return buffer.retainedDuplicate()
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            return buffer.retainedDuplicate()
        }
    },

    BZIP2(1, Integer.BYTES) {
        private val BLOCK_SIZE = 1

        private val HEADER = "BZh$BLOCK_SIZE".toByteArray(Charsets.US_ASCII)

        override fun compress(buffer: ByteBuf): ByteBuf {
            val view = buffer.duplicate()
            val decompressedSize = view.readableBytes()
            val outputBuffer = Unpooled.buffer()
            view.inputStream().use { input ->
                BZip2CompressorOutputStream(outputBuffer.outputStream(), BLOCK_SIZE).use { output ->
                    input.copyTo(output)
                }
            }
            val header = outputBuffer.getArray(0, HEADER.size)
            check(header.contentEquals(HEADER)) { "Invalid header: ${String(header, Charsets.US_ASCII)}" }
            outputBuffer.setInt(0, decompressedSize) // replace bzip2 header with decompressedSize, both 4 bytes
            return outputBuffer
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            val view = buffer.duplicate()
            val expectedDecompressedSize = view.readInt()
            val outputBuffer = Unpooled.buffer(expectedDecompressedSize)
            BZip2CompressorInputStream(HEADER.inputStream() + view.inputStream()).use { input ->
                outputBuffer.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val decompressedSize = outputBuffer.readableBytes()
            check(decompressedSize == expectedDecompressedSize) {
                "Decompressed size ($decompressedSize) != expected ($expectedDecompressedSize)"
            }
            return outputBuffer
        }
    },

    GZIP(2, Integer.BYTES) {
        override fun compress(buffer: ByteBuf): ByteBuf {
            val view = buffer.duplicate()
            val outputBuffer = Unpooled.buffer()
            outputBuffer.writeInt(view.readableBytes())
            view.inputStream().use { input ->
                GzipCompressorOutputStream(outputBuffer.outputStream()).use { output ->
                    input.copyTo(output)
                }
            }
            return outputBuffer
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            val view = buffer.duplicate()
            val expectedDecompressedSize = view.readInt()
            val outputBuffer = Unpooled.buffer(expectedDecompressedSize)
            GzipCompressorInputStream(view.inputStream()).use { input ->
                outputBuffer.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val decompressedSize = outputBuffer.readableBytes()
            check(decompressedSize == expectedDecompressedSize) {
                "Decompressed size ($decompressedSize) != expected ($expectedDecompressedSize)"
            }
            return outputBuffer
        }
    };

    abstract fun decompress(buffer: ByteBuf): ByteBuf

    abstract fun compress(buffer: ByteBuf): ByteBuf

    companion object {
        val LOOKUP = values().associateBy { it.id }
    }
}