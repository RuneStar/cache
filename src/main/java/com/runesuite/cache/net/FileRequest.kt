package com.runesuite.cache.net

import io.netty.buffer.ByteBuf

data class FileRequest(val fileId: FileId) : Request() {

    override fun write(output: ByteBuf) {
        output.writeByte(if (fileId.index == 255) 1 else 0)
                .writeByte(fileId.index)
                .writeShort(fileId.file)
    }

    override fun toString(): String {
        return "FileRequest(fileId=$fileId)"
    }
}