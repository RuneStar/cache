package com.runesuite.cache.format.net

import com.runesuite.cache.format.Archive
import com.runesuite.cache.format.ArchiveId
import io.netty.buffer.ByteBuf

class FileResponse(override val input: ByteBuf) : Response(input) {

    companion object {
        const val HEADER_LENGTH = java.lang.Byte.BYTES + java.lang.Short.BYTES
    }

    val archiveId = ArchiveId(input.getUnsignedByte(0).toInt(), input.getUnsignedShort(1))

    val archive = Archive(input.slice().skipBytes(HEADER_LENGTH))

    override fun toString(): String {
        return "FileResponse(archiveId=$archiveId, archive=$archive)"
    }
}