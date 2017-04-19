package com.runesuite.cache

import com.runesuite.cache.extensions.*
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.util.AsciiString
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream

enum class Compressor(val id: Int, val headerLength: Int) {

    NONE(0, 0) {
        override fun compress(buffer: ByteBuf): ByteBuf {
            return buffer
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            return buffer
        }
    },

    BZIP2(1, Integer.BYTES) {
        private val BLOCK_SIZE = 1

        private val HEADER: AsciiString = "BZh$BLOCK_SIZE".toAscii()

        override fun compress(buffer: ByteBuf): ByteBuf {
            buffer.inputStream().use { input ->
                val bufferOutput = Unpooled.buffer().outputStream()
                bufferOutput.writeInt(buffer.readableBytes())
                BZip2CompressorOutputStream(bufferOutput, BLOCK_SIZE).use { output ->
                    input.copyTo(output)
                    val buf = bufferOutput.buffer()
                    val header = buf.readArray(HEADER.length).asAscii()
                    check(header.contentEquals(HEADER)) { "Invalid header: $header" }
                    return buf
                }
            }
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            val expectedDecompressedSize = buffer.readInt()
            BZip2CompressorInputStream(HEADER.array().inputStream() + buffer.inputStream()).use { input ->
                Unpooled.buffer(expectedDecompressedSize).outputStream().use { output ->
                    input.copyTo(output)
                    val decompressedSize = output.buffer().readableBytes()
                    check(decompressedSize == expectedDecompressedSize)
                    return output.buffer()
                }
            }
        }
    },

    GZIP(2, Integer.BYTES) {
        override fun compress(buffer: ByteBuf): ByteBuf {
            buffer.inputStream().use { input ->
                val bufferOutput = Unpooled.buffer().outputStream()
                bufferOutput.writeInt(buffer.readableBytes())
                GzipCompressorOutputStream(bufferOutput).use { output ->
                    input.copyTo(output)
                    return bufferOutput.buffer()
                }
            }
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            val expectedDecompressedSize = buffer.readInt()
            GzipCompressorInputStream(buffer.inputStream()).use { input ->
                Unpooled.buffer(expectedDecompressedSize).outputStream().use { output ->
                    input.copyTo(output)
                    val decompressedSize = output.buffer().readableBytes()
                    check(decompressedSize == expectedDecompressedSize)
                    return output.buffer()
                }
            }
        }
    };

    abstract fun decompress(buffer: ByteBuf): ByteBuf

    abstract fun compress(buffer: ByteBuf): ByteBuf

    companion object {
        val LOOKUP = values().associateBy { it.id }
    }
}