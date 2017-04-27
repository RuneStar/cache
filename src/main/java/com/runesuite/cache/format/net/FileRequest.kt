package com.runesuite.cache.format.net

import io.netty.buffer.ByteBuf

internal data class FileRequest(val index: Int, val archive: Int) : Request() {

    override fun write(output: ByteBuf) {
        output.writeByte(if (index == 255) 1 else 0)
                .writeByte(index)
                .writeShort(archive)
    }
}