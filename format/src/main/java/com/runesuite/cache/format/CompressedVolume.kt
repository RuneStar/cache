package com.runesuite.cache.format

import com.google.common.hash.Hashing
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

internal class CompressedVolume(val buffer: ByteBuf) : Volume {

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

    override val crc: Int by lazy { Hashing.crc32().hashBytes(buffer.nioBuffer()).asInt() }

    override val decompressed: ByteBuf by lazy { compressor.decompress(compressed) }

    companion object {

        @JvmStatic
        fun fromVolume(volume: Volume): CompressedVolume {
            val header = Unpooled.buffer(5)
            header.writeByte(volume.compressor.id.toInt())
            val compressed = volume.compressed
            header.writeInt(compressed.readableBytes() - volume.compressor.headerLength)
            val data = Unpooled.compositeBuffer(2)
            data.addComponent(true, header)
            data.addComponent(true, compressed.retain())
            return CompressedVolume(data)
        }
    }
}