package com.runesuite.cache

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil

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

fun ByteBuf.readSliceMax(maxLength: Int): ByteBuf {
    return readSlice(Math.min(maxLength, readableBytes()))
}

inline fun ByteBuf.forEach(crossinline action: (Byte) -> Unit) {
    forEachByte {
        action(it)
        true
    }
}

//fun ByteBuf.readString(charset: Charset = RuneScape.CHARSET): String {
//    val length = bytesBefore(0)
//    check(length != -1)
//    val s = toString(readerIndex(), length, charset)
//    skipBytes(length + 1)
//    return s
//}