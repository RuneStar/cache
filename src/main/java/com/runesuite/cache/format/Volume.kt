package com.runesuite.cache.format

import com.runesuite.cache.extensions.update
import com.runesuite.cache.extensions.value32
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.util.zip.CRC32

interface Volume {

    val compressor: Compressor get() = Compressor.NONE

    val compressed: ByteBuf get() = compressor.compress(decompressed)

    val decompressed: ByteBuf get() = compressor.decompress(compressed)

    val version: Int? get() = null

    val crc: Int get() {
        return CRC32().run {
            update(buffer)
            value32
        }
    }

    val buffer: ByteBuf get() {
        return Unpooled.buffer(HEADER_LENGTH + compressed.readableBytes() + FOOTER_LENGTH).apply {
            writeByte(compressor.id.toInt())
            writeInt(compressed.readableBytes() - compressor.headerLength)
            writeBytes(decompressed)
            version?.let {
                writeShort(it)
            }
        }
    }

    companion object {
        const val HEADER_LENGTH = java.lang.Byte.BYTES + Integer.BYTES
        const val FOOTER_LENGTH = java.lang.Short.BYTES
    }
}