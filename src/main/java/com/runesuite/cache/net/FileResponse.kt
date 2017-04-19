package com.runesuite.cache.net

import com.runesuite.cache.CompressedFile
import io.netty.buffer.ByteBuf

class FileResponse(override val input: ByteBuf) : Response(input) {

    companion object {
        const val HEADER_LENGTH = java.lang.Byte.BYTES + java.lang.Short.BYTES
    }

    val fileId = FileId(input.getUnsignedByte(0).toInt(), input.getUnsignedShort(1))

    val compressedFile = CompressedFile.read(input.slice().skipBytes(HEADER_LENGTH))

    override fun toString(): String {
        return "FileResponse(fileId=$fileId, compressedFile=$compressedFile)"
    }
}