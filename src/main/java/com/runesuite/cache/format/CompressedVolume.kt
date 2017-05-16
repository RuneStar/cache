package com.runesuite.cache.format

import io.netty.buffer.ByteBuf

class CompressedVolume(override val buffer: ByteBuf) : Volume {

    override val compressor: Compressor

    override val compressed: ByteBuf

    override val version: Int?

    companion object {

        fun isValid(buffer: ByteBuf): Boolean {
            if (buffer.readableBytes() < Volume.HEADER_LENGTH) {
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
        buffer.markReaderIndex()
        val compressorId = buffer.readByte()
        compressor = checkNotNull(Compressor.LOOKUP[compressorId])
        val compressedLength = buffer.readInt() + compressor.headerLength
        compressed = buffer.readSlice(compressedLength)
        version = if (buffer.readableBytes() >= Volume.FOOTER_LENGTH) {
            buffer.readUnsignedShort()
        } else {
            null
        }
        buffer.resetReaderIndex()
    }

    override val crc: Int by lazy { super.crc }

    override val decompressed: ByteBuf by lazy { super.decompressed }
}