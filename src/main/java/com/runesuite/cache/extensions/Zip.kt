package com.runesuite.cache.extensions

import io.netty.buffer.ByteBuf
import java.util.zip.CRC32

fun CRC32.update(buffer: ByteBuf) {
    // io.netty.handler.codec.compression.ByteBufChecksum
    if (buffer.hasArray()) {
        update(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes())
    } else if (buffer.nioBufferCount() == 1) {
        update(buffer.internalNioBuffer(buffer.readerIndex(), buffer.readableBytes()))
    } else {
        buffer.nioBuffers().forEach {
            update(it)
        }
    }
}

val CRC32.value32: Int get() = value.toInt()