package org.runestar.cache.format

import com.google.common.hash.Hashing
import io.netty.buffer.ByteBuf

class Volume(
        val buffer: ByteBuf
) {

    val compressed: ByteBuf

    val compressor: Compressor

    val version: Int?

    init {
        buffer.markReaderIndex()
        compressor = Compressor.of(buffer.readByte())
        val compressedLength = buffer.readInt() + compressor.headerLength
        compressed = buffer.readSlice(compressedLength)
        version = if (buffer.readableBytes() == 2) buffer.readUnsignedShort() else null
        buffer.resetReaderIndex()
    }

    val crc: Int get() = Hashing.crc32().hashBytes(buffer.nioBuffer()).asInt()

    fun decompress(xteaKey: IntArray? = null): ByteBuf {
        val decrypted = if (xteaKey != null) {
            val copy = compressed.copy()
            XteaCipher.decrypt(copy, xteaKey)
            copy
        } else {
            compressed
        }
        return compressor.decompress(decrypted)
    }

    override fun toString(): String {
        return "Volume(compressor=$compressor, version=$version, crc=$crc, compressed=a$compressed)"
    }
}