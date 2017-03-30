package com.runesuite.cache.buf

import io.netty.buffer.ByteBuf

internal fun ByteBuf.readableToString(): String {
    val array = ByteArray(readableBytes())
    getBytes(readerIndex(), array)
    return array.contentToString()
}