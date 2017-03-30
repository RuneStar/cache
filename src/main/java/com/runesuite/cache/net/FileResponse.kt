package com.runesuite.cache.net

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

data class FileResponse(override val input: ByteBuf) : Response(input) {

    companion object {
        const val SIZE = 512
    }

    val index get() = input.getUnsignedByte(0).toInt()

    val file get() = input.getUnsignedShort(1)

    val compression get() = input.getUnsignedByte(3).toInt()

    val compressedFileSize get() = input.getInt(4)

    val compressedData: ByteBuf by lazy {
        val array = ByteArray(size)
        val view = input.slice()
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
        if (left % (SIZE - 1) == 0) {
            return left / (SIZE - 1)
        } else {
            return left / (SIZE - 1) + 1
        }
    }

    val headerDone = input.readableBytes() >= 8

    val done = headerDone && size + 3 + breaks <= input.readableBytes()

    override fun toString(): String {
        return "FileResponse(done=$done, index=$index, file=$file, compression=$compression, compressedFileSize=$compressedFileSize)"
    }
}