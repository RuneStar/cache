package org.runestar.cache.format.fs

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.util.internal.PlatformDependent
import org.runestar.cache.format.closeQuietly
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.Channel
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption

internal class BufFile
@Throws(IOException::class)
constructor(val file: Path, val maxSize: Int) : Channel {

    private val fileChannel = FileChannel.open(file,
            StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)

    private val mappedByteBuffer: MappedByteBuffer

    val buffer: ByteBuf

    override fun isOpen() = fileChannel.isOpen

    init {
        val originalSize: Long
        try {
            originalSize = fileChannel.size()
        } catch (sizeException: IOException) {
            fileChannel.closeQuietly(sizeException)
            throw sizeException
        }
        try {
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, maxSize.toLong())
        } catch (mapException: IOException) {
            try {
                fileChannel.truncate(originalSize)
            } catch (truncateException: IOException) {
                mapException.addSuppressed(truncateException)
            } finally {
                fileChannel.closeQuietly(mapException)
            }
            throw mapException
        }
        buffer = Unpooled.wrappedBuffer(mappedByteBuffer)
        buffer.writerIndex(originalSize.toInt())
        check(buffer.capacity() == maxSize)
        check(buffer.readerIndex() == 0)
        check(buffer.writerIndex() == originalSize.toInt())
    }

    @Throws(IOException::class)
    override fun close() {
        if (isOpen) {
            val writtenSize = buffer.writerIndex()
            PlatformDependent.freeDirectBuffer(mappedByteBuffer.force())
            fileChannel.use {
                it.truncate(writtenSize.toLong())
            }
        }
    }
}