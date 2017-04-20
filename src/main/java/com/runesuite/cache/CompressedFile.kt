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

    val compressor: Compressor get() = checkNotNull(Compressor.LOOKUP[buffer.getRelativeUnsignedByte(0).toInt()])

    val doneDataLength: Int get() = compressor.headerLength + buffer.getRelativeInt(1)

    val data: ByteBuf get() = buffer.sliceRelative(HEADER_LENGTH, doneDataLength)

    val version: Int? get() {
        return if (buffer.readableBytes() > HEADER_LENGTH + doneDataLength) {
            buffer.getRelativeUnsignedShort(HEADER_LENGTH + doneDataLength)
        } else {
            null
        }
    }

    val done get() = buffer.readableBytes() >= doneDataLength + HEADER_LENGTH

    fun decompress(): ByteBuf {
        check(done)
        return compressor.decompress(data.slice())
    }

    val crc: Int get() = Crc32.checksum(buffer.sliceRelative(0, HEADER_LENGTH + doneDataLength))

    override fun toString(): String {
        return "CompressedFile(compressor=$compressor, doneDataLength=$doneDataLength, done=$done, version=$version)"
    }
}