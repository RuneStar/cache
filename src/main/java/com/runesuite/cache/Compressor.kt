package com.runesuite.cache

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.Unpooled
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream

enum class Compressor(val id: Int) {

    NONE(0) {
        override fun compress(buffer: ByteBuf): ByteBuf {
            return buffer
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            return buffer
        }
    },

    BZIP2(1) {
        private val HEADER = byteArrayOf('B'.toByte(), 'Z'.toByte(), 'h'.toByte(), 1)

        override fun compress(buffer: ByteBuf): ByteBuf {
            val view = buffer.slice()
            ByteBufInputStream(view).use { i ->
                val b = ByteBufOutputStream(Unpooled.buffer())
                b.writeInt(view.readableBytes())
                BZip2CompressorOutputStream(b).use { o ->
                    i.copyTo(o)
                    val buf = b.buffer()
                    val header = ByteArray(HEADER.size)
                    buf.readBytes(header, 0, header.size)
                    check(header.contentEquals(HEADER)) { "Invalid header ${header.contentToString()}" }
                    return buf
                }
            }
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            val view = buffer.slice()
            val decompressedSize = view.readInt()
            BZip2CompressorInputStream(ByteBufInputStream(view)).use { i ->
                ByteBufOutputStream(Unpooled.buffer(decompressedSize)).use { o ->
                    o.write(HEADER)
                    o.write(i.readBytes())
//                    check(o.writtenBytes() == decompressedSize + HEADER.size)
                    return o.buffer()
                }
            }
        }
    },

    GZIP(2) {
        override fun compress(buffer: ByteBuf): ByteBuf {
            val view = buffer.slice()
            ByteBufInputStream(view).use { i ->
                val b = ByteBufOutputStream(Unpooled.buffer())
                b.writeInt(view.readableBytes())
                GzipCompressorOutputStream(b).use { o ->
                    i.copyTo(o)
                    return b.buffer()
                }
            }
        }

        override fun decompress(buffer: ByteBuf): ByteBuf {
            val view = buffer.slice()
            val decompressedSize = view.readInt()
            GzipCompressorInputStream(ByteBufInputStream(view)).use { i ->
                ByteBufOutputStream(Unpooled.buffer(decompressedSize)).use { o ->
                    i.copyTo(o)
//                    check(o.writtenBytes() == decompressedSize)
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