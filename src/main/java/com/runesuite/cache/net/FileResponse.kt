package com.runesuite.cache.net

import io.netty.buffer.ByteBuf

data class FileResponse(override val byteBuf: ByteBuf) : Response(byteBuf) {

    companion object {
        const val SIZE = 512
    }

    val index: Int

    val file: Int

    val compression: Int

    val compressedFileSize: Int

    init {
        val sliced = byteBuf.slice()
        index = sliced.readUnsignedByte().toInt()
        file = sliced.readUnsignedShort()
        compression = sliced.readUnsignedByte().toInt()
        compressedFileSize = sliced.readInt()
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

    val done get() = headerDone && size + 3 + breaks <= byteBuf.readableBytes()

    val headerDone get() = byteBuf.readableBytes() >= 8
}