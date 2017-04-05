package com.runesuite.cache

import com.runesuite.cache.extensions.inputStream
import com.runesuite.cache.extensions.outputStream
import com.runesuite.cache.extensions.plus
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
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

    BZIP2(1, 4) {
        private val BLOCK_SIZE = 1

        private val HEADER = byteArrayOf('B'.toByte(), 'Z'.toByte(), 'h'.toByte(), BLOCK_SIZE.toString()[0].toByte())

        override fun compress(buffer: ByteBuf): ByteBuf {
            buffer.slice().inputStream().use { i ->
                val b = Unpooled.buffer().outputStream()
                b.writeInt(buffer.readableBytes())
                BZip2CompressorOutputStream(b, BLOCK_SIZE).use { o ->
                    i.copyTo(o)
                    val buf = b.buffer()
                    val header = ByteArray(HEADER.size)
                    buf.readBytes(header, 0, header.size)
                    check(header.contentEquals(HEADER)) { "Invalid header: ${header.contentToString()}" }
                    return buf
                }
            }
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            val view = buffer.slice()
            val expectedDecompressedSize = view.readInt()
            BZip2CompressorInputStream(HEADER.inputStream() + view.inputStream()).use { i ->
                Unpooled.buffer(expectedDecompressedSize).outputStream().use { o ->
                    i.copyTo(o)
                    val decompressedSize = o.buffer().readableBytes()
                    check(decompressedSize == expectedDecompressedSize)
                    return o.buffer()
                }
            }
        }
    },

    GZIP(2, 4) {
        override fun compress(buffer: ByteBuf): ByteBuf {
            buffer.slice().inputStream().use { i ->
                val b = Unpooled.buffer().outputStream()
                b.writeInt(buffer.readableBytes())
                GzipCompressorOutputStream(b).use { o ->
                    i.copyTo(o)
                    return b.buffer()
                }
            }
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            val view = buffer.slice()
            val expectedDecompressedSize = view.readInt()
            GzipCompressorInputStream(view.inputStream()).use { i ->
                Unpooled.buffer(expectedDecompressedSize).outputStream().use { o ->
                    i.copyTo(o)
                    val decompressedSize = o.buffer().readableBytes()
                    check(decompressedSize == expectedDecompressedSize)
                    return o.buffer()
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