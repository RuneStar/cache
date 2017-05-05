package com.runesuite.cache.format

import com.runesuite.cache.extensions.*
import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import io.netty.util.AsciiString
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream

enum class Compressor(val id: Byte, val headerLength: Int) {

    NONE(0, 0) {
        override fun compress(buffer: ByteBuf): ByteBuf {
            return buffer.retain()
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            return buffer.retain()
        }
    },

    BZIP2(1, Integer.BYTES) {
        private val BLOCK_SIZE = 1

        private val HEADER: AsciiString = "BZh$BLOCK_SIZE".toAscii()

        override fun compress(buffer: ByteBuf): ByteBuf {
            val view = buffer.duplicate()
            val decompressedSize = view.readableBytes()
            val outputBuffer = PooledByteBufAllocator.DEFAULT.buffer()
            view.inputStream().use { input ->
                BZip2CompressorOutputStream(outputBuffer.outputStream(), BLOCK_SIZE).use { output ->
                    input.copyTo(output)
                }
            }
            val header = outputBuffer.getArray(0, HEADER.length).asAscii()
            check(header.contentEquals(HEADER)) { "Invalid header: $header" }
            outputBuffer.setInt(0, decompressedSize) // replace bzip2 header with decompressedSize, both 4 bytes
            return outputBuffer
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            val view = buffer.duplicate()
            val expectedDecompressedSize = view.readInt()
            val outputBuffer = PooledByteBufAllocator.DEFAULT.buffer(expectedDecompressedSize)
            BZip2CompressorInputStream(HEADER.array().inputStream() + view.inputStream()).use { input ->
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
            val outputBuffer = PooledByteBufAllocator.DEFAULT.buffer()
            outputBuffer.writeInt(view.readableBytes())
            view.slice().inputStream().use { input ->
                GzipCompressorOutputStream(outputBuffer.outputStream()).use { output ->
                    input.copyTo(output)
                }
            }
            return outputBuffer
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            val view = buffer.duplicate()
            val expectedDecompressedSize = view.readInt()
            val outputBuffer = PooledByteBufAllocator.DEFAULT.buffer(expectedDecompressedSize)
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