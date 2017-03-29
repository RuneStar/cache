package com.runesuite.cache.net

import io.netty.buffer.Unpooled

data class FileRequest(val index: Int, val file: Int) : Request(){

    override val byteBuf get() = Unpooled.buffer(4)
            .writeByte(if (index == 255) 1 else 0)
            .writeByte(index)
            .writeShort(file)
}