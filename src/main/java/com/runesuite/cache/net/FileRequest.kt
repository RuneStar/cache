package com.runesuite.cache.net

import com.runesuite.cache.ArchiveId
import io.netty.buffer.ByteBuf

internal data class FileRequest(val archiveId: ArchiveId) : Request() {

    override fun write(output: ByteBuf) {
        output.writeByte(if (archiveId.index == 255) 1 else 0)
                .writeByte(archiveId.index)
                .writeShort(archiveId.archive)
    }

    override fun toString(): String {
        return "FileRequest(archiveId=$archiveId)"
    }
}