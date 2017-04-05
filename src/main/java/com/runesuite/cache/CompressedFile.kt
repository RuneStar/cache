package com.runesuite.cache

import com.runesuite.cache.extensions.readSliceMax
import io.netty.buffer.ByteBuf

class CompressedFile(val compressor: Compressor, val doneDataLength: Int, val data: ByteBuf, val version: Int?) {

    companion object {
        const val HEADER_LENGTH = 5

        fun read(buffer: ByteBuf): CompressedFile {
            val compressor = checkNotNull(Compressor.LOOKUP[buffer.readUnsignedByte().toInt()])
            val doneDataLength = buffer.readInt() + compressor.headerLength
            val data = buffer.readSliceMax(doneDataLength)
            val version = if (buffer.readableBytes() >= 2) {
                buffer.readUnsignedShort()
            } else {
                null
            }
            return CompressedFile(compressor, doneDataLength, data, version)
        }
    }

    val done get() = data.readableBytes() == doneDataLength

    fun decompress(): ByteBuf {
        check(done)
        return compressor.decompress(data.slice())
    }

    override fun toString(): String {
        return "CompressedFile(compressor=$compressor, doneDataLength=$doneDataLength, done=$done, version=$version)"
    }
}