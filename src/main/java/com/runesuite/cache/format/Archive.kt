package com.runesuite.cache.format

import com.runesuite.cache.format.Compressor
import com.runesuite.cache.format.Crc32
import io.netty.buffer.ByteBuf

class Archive(val buffer: ByteBuf) {

    val compressor: Compressor

    val compressedLength: Int

    val compressed: ByteBuf

    val version: Int?

    companion object {
        const val HEADER_LENGTH = java.lang.Byte.BYTES + Integer.BYTES

        fun isValid(buffer: ByteBuf): Boolean {
            if (buffer.readableBytes() < HEADER_LENGTH) {
                return false
            }
            val view = buffer.slice()
            val compressorId = view.readUnsignedByte().toInt()
            val compressor = checkNotNull(Compressor.LOOKUP[compressorId])
            val compressedLength = view.readInt() + compressor.headerLength
            if (view.readableBytes() < compressedLength) {
                return false
            }
            return true
        }
    }

    init {
        require(isValid(buffer))
        val view = buffer.slice()
        val compressorId = view.readUnsignedByte().toInt()
        compressor = checkNotNull(Compressor.LOOKUP[compressorId])
        compressedLength = view.readInt() + compressor.headerLength
        compressed = view.readSlice(compressedLength)
        version = if (view.readableBytes() >= java.lang.Short.BYTES) {
            view.readUnsignedShort()
        } else {
            null
        }
    }

    val crc: Int by lazy { Crc32.checksum(buffer.slice(buffer.readerIndex(), HEADER_LENGTH + compressedLength)) }

    val data: ByteBuf by lazy { compressor.decompress(compressed.slice()) }

//    fun write(buffer: ByteBuf) {
//        buffer.writeByte(compressor.id)
//        buffer.writeInt(compressedLength - compressor.headerLength)
//        buffer.writeBytes(compressed)
//        if (version != null) {
//            buffer.writeShort(version)
//        }
//    }
}