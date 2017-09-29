package com.runesuite.cache.format

import com.hunterwb.kxtra.nettybuffer.checksum.update
import io.netty.buffer.ByteBuf
import java.util.zip.CRC32

class CompressedVolume(val buffer: ByteBuf) : Volume {

    override val compressed: ByteBuf

    override val compressor: Compressor

    override val version: Int?

    init {
        buffer.markReaderIndex()
        val compressorId = buffer.readByte()
        compressor = requireNotNull(Compressor.LOOKUP[compressorId]) { "unknown compressor id: $compressorId" }
        val compressedLength = buffer.readInt() + compressor.headerLength
        compressed = buffer.readSlice(compressedLength)
        version = if (buffer.readableBytes() == 2) buffer.readUnsignedShort() else null
        buffer.resetReaderIndex()
    }

    override val crc: Int by lazy {
        CRC32().run {
            update(buffer)
            value.toInt()
        }
    }

    override val decompressed: ByteBuf by lazy { compressor.decompress(compressed) }
}