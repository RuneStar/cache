package com.runesuite.cache.net

import com.runesuite.cache.buffer.asList
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

data class FileResponse(override val byteBuf: ByteBuf) : Response(byteBuf) {

    companion object {
        const val SIZE = 512
    }

    val index get() = byteBuf.getUnsignedByte(0).toInt()

    val file get() = byteBuf.getUnsignedShort(1)

    val compression get() = byteBuf.getUnsignedByte(3).toInt()

    val compressedFileSize get() = byteBuf.getInt(4)

    val compressedData: ByteBuf by lazy {
        val array = ByteArray(size)
        val view = byteBuf.slice()
        var totalRead = 3
        view.skipBytes(totalRead)
        var compressedDataOffset = 0
        for (i in 0..breaks) {
            val bytesInBlock = SIZE - (totalRead % SIZE)
            val bytesToRead = Math.min(bytesInBlock, size - compressedDataOffset)
            view.getBytes(view.readerIndex(), array, compressedDataOffset, bytesToRead)
            view.skipBytes(bytesToRead)
            compressedDataOffset += bytesToRead
            totalRead += bytesToRead
            if (i < breaks) {
                check(compressedDataOffset < size)
                val b = view.readUnsignedByte().toInt()
                totalRead++
                check(b == 0xff)
            }
        }
        check(compressedDataOffset == size)
        Unpooled.wrappedBuffer(array)
    }

    val size get() = compressedFileSize + 5 + (if (compression == 0) 0 else 4)

    val breaks: Int get() {
        val initialSize = SIZE - 3
        if (size <= initialSize) {
            return 0
        }
        val left = size - initialSize
        if (left % 511 == 0) {
            return left / 511
        } else {
            return left / 511 + 1
        }
    }

    val headerDone = byteBuf.readableBytes() >= 8

    val done = headerDone && size + 3 + breaks <= byteBuf.readableBytes()

    override fun toString(): String {
        if (!headerDone) {
            return "FileResponse(headerDone=$headerDone, byteBuf=${byteBuf.asList()})"
        } else if (!done) {
            return "FileResponse(headerDone=$headerDone, done=$done, index=$index, file=$file, compression=$compression, compressedFileSize=$compressedFileSize, byteBuf=${byteBuf.asList()})"
        } else {
            return "FileResponse(headerDone=$headerDone, done=$done, index=$index, file=$file, compression=$compression, compressedFileSize=$compressedFileSize, byteBuf=${byteBuf.asList()})"
        }
    }
}