package com.runesuite.cache.extensions

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.ByteBufUtil
import java.nio.IntBuffer
import java.nio.ShortBuffer

fun ByteBuf.readableArray(): ByteArray {
    return getArray(readerIndex(), readableBytes())
}

fun ByteBuf.getArray(index: Int, length: Int): ByteArray {
    return ByteBufUtil.getBytes(this, index, length)
}

fun ByteBuf.readArray(length: Int): ByteArray {
    val a = getArray(readerIndex(), length)
    skipBytes(length)
    return a
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
    val byteLength = length * Integer.BYTES
    val b = nioBuffer(readerIndex(), byteLength).asIntBuffer()
    skipBytes(byteLength)
    return b
}

fun ByteBuf.readSliceAsShorts(length: Int): ShortBuffer {
    val byteLength = length * java.lang.Short.BYTES
    val b = nioBuffer(readerIndex(), byteLength).asShortBuffer()
    skipBytes(byteLength)
    return b
}

fun ByteBuf.forEach(action: (Byte) -> Unit) {
    forEachByte {
        action(it)
        true
    }
}