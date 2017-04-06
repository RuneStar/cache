package com.runesuite.cache.net

import com.runesuite.cache.CompressedFile
import io.netty.buffer.ByteBuf

class FileResponse(override val input: ByteBuf) : Response(input) {

    companion object {
        const val CHUNK_LENGTH = 512
        const val HEADER_LENGTH = 3

        // todo
        fun breaksCount(length: Int): Int {
            return if (length <= FileResponse.CHUNK_LENGTH) {
                0
            } else {
                Math.ceil((length - FileResponse.CHUNK_LENGTH).toDouble() / (FileResponse.CHUNK_LENGTH - 1).toDouble()).toInt()
            }
        }

        // todo
        fun nextBreakAfter(length: Int): Int {
            return if (length <= FileResponse.CHUNK_LENGTH) {
                FileResponse.CHUNK_LENGTH - length
            } else {
                ((FileResponse.CHUNK_LENGTH - 1) - ((length - FileResponse.CHUNK_LENGTH) % (FileResponse.CHUNK_LENGTH - 1))) % (FileResponse.CHUNK_LENGTH - 1)
            }
        }
    }

    val fileId = FileId(input.getUnsignedByte(0).toInt(), input.getUnsignedShort(1))

    val compressedFile = CompressedFile.read(input.slice().skipBytes(HEADER_LENGTH))

    override fun toString(): String {
        return "FileResponse(fileId=$fileId, compressedFile=$compressedFile)"
    }
}