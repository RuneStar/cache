package com.runesuite.cache.net

data class HandshakeRequest(val revision: Int) : Request() {

    override val byteBuf get() = io.netty.buffer.Unpooled.buffer(5).writeByte(15).writeInt(revision)
}