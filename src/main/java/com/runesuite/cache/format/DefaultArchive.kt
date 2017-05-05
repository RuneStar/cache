package com.runesuite.cache.format

import io.netty.buffer.ByteBuf

class DefaultArchive(override val buffer: ByteBuf) : Archive {

    override val compressor: Compressor

    override val compressed: ByteBuf

    override val version: Int?

    companion object {

        fun isValid(buffer: ByteBuf): Boolean {
            if (buffer.readableBytes() < Archive.HEADER_LENGTH) {
                return false
            }
            val view = buffer.duplicate()
            val compressorId = view.readByte()
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
        val view = buffer.duplicate()
        val compressorId = view.readByte()
        compressor = checkNotNull(Compressor.LOOKUP[compressorId])
        val compressedLength = view.readInt() + compressor.headerLength
        compressed = view.readSlice(compressedLength)
        version = if (view.readableBytes() >= Archive.FOOTER_LENGTH) {
            view.readUnsignedShort()
        } else {
            null
        }
    }

    override val crc: Int by lazy { super.crc }

    override val decompressed: ByteBuf by lazy { super.decompressed }
}