package com.runesuite.cache.content.def

import com.runesuite.cache.extensions.readableArray
import io.netty.buffer.ByteBuf

class TextureDefinition : CacheDefinition {

    var fileIds: IntArray? = null

    override fun read(buffer: ByteBuf) {
        println(buffer.readableArray().contentToString())
        buffer.skipBytes(3)
        val length = buffer.readUnsignedByte().toInt()
        fileIds = IntArray(length) {
            buffer.readUnsignedShort()
        }
    }

    override fun toString(): String {
        return "TextureDefinition(fileIds=${fileIds?.contentToString()})"
    }
}