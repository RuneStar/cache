package com.runesuite.cache.net

import io.netty.buffer.ByteBuf

data class FileRequest(val index: Int, val file: Int) : Request() {

    override fun write(output: ByteBuf) {
        output.writeByte(if (index == 255) 1 else 0)
                .writeByte(index)
                .writeShort(file)
    }

    override fun toString(): String {
        return "FileRequest(index=$index, file=$file)"
    }
}