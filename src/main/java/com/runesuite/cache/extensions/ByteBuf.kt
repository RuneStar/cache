package com.runesuite.cache.extensions

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import java.nio.IntBuffer

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

fun ByteBuf.readSliceMax(maxLength: Int): ByteBuf {
    return readSlice(Math.min(maxLength, readableBytes()))
}

internal fun ByteBuf.readSmartInt(): Int {
    return if (getByte(readerIndex()) < 0) {
        readInt() and 0x7F_FF_FF_FF
    } else {
        readUnsignedShort()
    }
}

fun ByteBuf.readSliceAsInts(length: Int): IntBuffer {
    val b = nioBuffer(readerIndex(), length * 4).asIntBuffer()
    skipBytes(length * 4)
    return b
}