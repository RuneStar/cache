package com.runesuite.cache.extensions

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.ByteBufUtil
import java.nio.IntBuffer

fun ByteBuf.getRelativeByte(index: Int): Byte {
    return getByte(readerIndex() + index)
}

fun ByteBuf.getRelativeUnsignedByte(index: Int): Short {
    return getUnsignedByte(readerIndex() + index)
}

fun ByteBuf.getRelativeShort(index: Int): Short {
    return getShort(readerIndex() + index)
}

fun ByteBuf.getRelativeUnsignedShort(index: Int): Int {
    return getUnsignedShort(readerIndex() + index)
}

fun ByteBuf.getRelativeInt(index: Int): Int {
    return getInt(readerIndex() + index)
}

fun ByteBuf.getRelativeUnsignedInt(index: Int): Long {
    return getUnsignedInt(readerIndex() + index)
}

fun ByteBuf.sliceRelative(index: Int, length: Int): ByteBuf {
    return slice(index + readerIndex(), length)
}

fun ByteBuf.hexToString(): String {
    return ByteBufUtil.hexDump(this)
}

fun ByteBuf.readableArray(): ByteArray {
    return ByteBufUtil.getBytes(this)
}

fun ByteBuf.readArray(length: Int): ByteArray {
    val a = ByteBufUtil.getBytes(this, readerIndex(), length)
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
    val b = nioBuffer(readerIndex(), length * 4).asIntBuffer()
    skipBytes(length * 4)
    return b
}