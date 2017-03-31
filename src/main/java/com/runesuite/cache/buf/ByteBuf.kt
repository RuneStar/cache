package com.runesuite.cache.buf

import io.netty.buffer.ByteBuf

internal fun ByteBuf.readableToString(): String {
    return readableArray().contentToString()
}

internal fun ByteBuf.readableArray(): ByteArray {
    val array = ByteArray(readableBytes())
    getBytes(readerIndex(), array)
    return array
}