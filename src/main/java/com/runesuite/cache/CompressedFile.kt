package com.runesuite.cache

import com.runesuite.cache.extensions.getRelativeInt
import com.runesuite.cache.extensions.getRelativeUnsignedByte
import com.runesuite.cache.extensions.getRelativeUnsignedShort
import com.runesuite.cache.extensions.sliceRelative
import io.netty.buffer.ByteBuf

class CompressedFile(val buffer: ByteBuf) {

    companion object {
        const val HEADER_LENGTH = java.lang.Byte.BYTES + Integer.BYTES
    }

    val compressor: Compressor = checkNotNull(Compressor.LOOKUP[buffer.getRelativeUnsignedByte(0).toInt()])

    val compressedDataLength: Int = compressor.headerLength + buffer.getRelativeInt(1)

    val compressedData: ByteBuf get() = buffer.sliceRelative(HEADER_LENGTH, compressedDataLength)

    val version: Int? = let {
        if (buffer.readableBytes() > HEADER_LENGTH + compressedDataLength) {
            buffer.getRelativeUnsignedShort(HEADER_LENGTH + compressedDataLength)
        } else {
            null
        }
    }

    val done get() = buffer.readableBytes() >= compressedDataLength + HEADER_LENGTH

    val data: ByteBuf by lazy {
        check(done)
        compressor.decompress(compressedData.slice())
    }

    val crc: Int by lazy { Crc32.checksum(buffer.sliceRelative(0, HEADER_LENGTH + compressedDataLength)) }

    fun write(buffer: ByteBuf) {
        buffer.writeByte(compressor.id)
        buffer.writeInt(compressedDataLength - compressor.headerLength)
        buffer.writeBytes(compressedData)
        if (version != null) {
            buffer.writeShort(version)
        }
    }

    override fun toString(): String {
        return "CompressedFile(compressor=$compressor, compressedDataLength=$compressedDataLength, done=$done, version=$version)"
    }
}