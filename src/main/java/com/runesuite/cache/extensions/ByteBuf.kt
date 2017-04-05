package com.runesuite.cache.extensions

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream

fun ByteBuf.readableToString(): String {
    return readableArray().contentToString()
}

fun ByteBuf.readableArray(): ByteArray {
    val array = ByteArray(readableBytes())
    getBytes(readerIndex(), array)
    return array
}

fun ByteBuf.inputStream(): ByteBufInputStream {
    return ByteBufInputStream(this)
}

fun ByteBuf.outputStream(): ByteBufOutputStream {
    return ByteBufOutputStream(this)
}