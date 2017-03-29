package com.runesuite.cache.net

import com.runesuite.cache.buffer.asList

data class HandshakeRequest(val revision: Int) : Request() {

    override val byteBuf = io.netty.buffer.Unpooled.buffer(5).writeByte(15).writeInt(revision)

    override fun toString(): String {
        return "HandshakeRequest(revision=$revision, byteBuf=${byteBuf.asList()})"
    }
}