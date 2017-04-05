package com.runesuite.cache

import com.runesuite.cache.extensions.readSliceMax
import io.netty.buffer.ByteBuf

class CompressedFile(val compressor: Compressor, val compressedDataLength: Int, val compressedData: ByteBuf, val version: Int?) {

    companion object {
        const val HEADER_LENGTH = 5

        fun read(buffer: ByteBuf): CompressedFile {
            val compressor = checkNotNull(Compressor.LOOKUP[buffer.readUnsignedByte().toInt()])
            val compressedDataLength = buffer.readInt() + compressor.headerLength
            val compressedData = buffer.readSliceMax(compressedDataLength)
            val version = if (buffer.readableBytes() >= 2) {
                buffer.readUnsignedShort()
            } else {
                null
            }
            return CompressedFile(compressor, compressedDataLength, compressedData, version)
        }
    }

    val done = compressedData.readableBytes() == compressedDataLength

    fun decompress(): ByteBuf {
        check(done)
        return compressor.decompress(compressedData.slice())
    }

    override fun toString(): String {
        return "CompressedFile(compressor=$compressor, compressedDataLength=$compressedDataLength, done=$done, version=$version)"
    }
}