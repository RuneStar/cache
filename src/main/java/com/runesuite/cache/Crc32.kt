package com.runesuite.cache

import io.netty.buffer.ByteBuf
import java.util.zip.CRC32

object Crc32 {

    fun checksum(bytes: ByteBuf): Int {
        val crc = CRC32()
        // io.netty.handler.codec.compression.ByteBufChecksum
        if (bytes.hasArray()) {
            crc.update(bytes.array(), bytes.arrayOffset() + bytes.readerIndex(), bytes.readableBytes())
        } else if (bytes.nioBufferCount() == 1) {
            crc.update(bytes.internalNioBuffer(bytes.readerIndex(), bytes.readableBytes()))
        } else {
            crc.update(bytes.nioBuffer())
        }
        return crc.value.toInt()
    }
}