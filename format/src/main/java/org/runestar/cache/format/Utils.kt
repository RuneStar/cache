package org.runestar.cache.format

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.ByteBufUtil
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.nio.LongBuffer
import java.nio.ShortBuffer

fun ByteBuf.inputStream(
        length: Int = readableBytes(),
        releaseOnClose: Boolean = false
): ByteBufInputStream {
    return ByteBufInputStream(this, length, releaseOnClose)
}

fun ByteBuf.outputStream(): ByteBufOutputStream {
    return ByteBufOutputStream(this)
}

/**
 * Exposes this buffer's sub-region as an NIO [ShortBuffer].
 *
 * @see[ByteBuf.nioBuffer]
 */
fun ByteBuf.nioShortBuffer(
        index: Int,
        length: Int
): ShortBuffer {
    return nioBuffer(index, java.lang.Short.SIZE / java.lang.Byte.SIZE * length).asShortBuffer()
}

fun ByteBuf.readNioShortBuffer(
        length: Int
): ShortBuffer {
    return nioShortBuffer(readerIndex(), length)
            .also { skipBytes(java.lang.Short.SIZE / java.lang.Byte.SIZE * length) }
}

/**
 * Exposes this buffer's sub-region as an NIO [IntBuffer].
 *
 * @see[ByteBuf.nioBuffer]
 */
fun ByteBuf.nioIntBuffer(
        index: Int,
        length: Int
): IntBuffer {
    return nioBuffer(index, Integer.SIZE / java.lang.Byte.SIZE * length).asIntBuffer()
}

fun ByteBuf.readNioIntBuffer(
        length: Int
): IntBuffer {
    return nioIntBuffer(readerIndex(), length)
            .also { skipBytes(Integer.SIZE / java.lang.Byte.SIZE * length) }
}

/**
 * Exposes this buffer's sub-region as an NIO [LongBuffer].
 *
 * @see[ByteBuf.nioBuffer]
 */
fun ByteBuf.nioLongBuffer(
        index: Int,
        length: Int
): LongBuffer {
    return nioBuffer(index, java.lang.Long.SIZE / java.lang.Byte.SIZE * length).asLongBuffer()
}

fun ByteBuf.readNioLongBuffer(
        length: Int
): LongBuffer {
    return nioLongBuffer(readerIndex(), length)
            .also { skipBytes(java.lang.Long.SIZE / java.lang.Byte.SIZE * length) }
}

/**
 * Create a copy of the underlying storage into a byte array.
 * The copy will start at [index] and copy [length] bytes.
 */
fun ByteBuf.toArray(
        index: Int,
        length: Int
): ByteArray {
    return ByteBufUtil.getBytes(this, index, length)
}

/**
 * Create a copy of the underlying storage into a byte array.
 * The copy will start at [ByteBuf.readerIndex] and copy [length] bytes.
 * [ByteBuf.readerIndex] will increase by [length].
 */
fun ByteBuf.readArray(
        length: Int = readableBytes()
): ByteArray {
    return toArray(readerIndex(), length).also { skipBytes(length) }
}

fun IntArray.asByteArray(): ByteArray {
    return ByteBuffer.allocate(size * (Integer.SIZE / java.lang.Byte.SIZE)).run {
        asIntBuffer().put(this@asByteArray)
        array()
    }
}

fun Short.toUnsignedInt(): Int = java.lang.Short.toUnsignedInt(this)

/**
 * Closes this resource, if an exception occurs it is [Throwable.addSuppressed] to [cause] but otherwise ignored.
 *
 * @param[cause] an exception encountered which is the reason why this resource is being closed, or null if there
 * is not one
 */
@JvmOverloads
fun AutoCloseable.closeQuietly(cause: Throwable? = null) {
    try {
        close()
    } catch (closeException: Throwable) {
        cause?.addSuppressed(closeException)
    }
}