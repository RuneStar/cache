package com.runesuite.cache.extensions

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.ByteBufUtil
import org.bouncycastle.jcajce.provider.digest.Whirlpool
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

inline fun ByteBuf.forEach(crossinline action: (Byte) -> Unit) {
    forEachByte {
        action(it)
        true
    }
}

private val whirlpoolDigest by lazy { Whirlpool.Digest() }

@Synchronized
fun ByteBuf.whirlpool(): ByteArray {
    // org.bouncycastle.crypto.digests.WhirlPoolDigest.update(byte[] in, int inOff, int len)
    forEach {
        whirlpoolDigest.update(it)
    }
    val hash = whirlpoolDigest.digest()
    check(hash.size == 64)
    return hash
}

internal fun ByteBuf.readRsString(): String {
    val sb = StringBuilder()
    while (true) {
        val b = readByte()
        if (b.toInt() == 0) {
            break
        }
        sb.append(b.toRsChar())
    }
    return sb.toString()
}