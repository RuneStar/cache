package com.runesuite.cache.net

import io.netty.buffer.ByteBuf

data class HandshakeRequest(val revision: Int) : Request() {

    override fun write(output: ByteBuf) {
        output.writeByte(15).writeInt(revision)
    }

    override fun toString(): String {
        return "HandshakeRequest(revision=$revision)"
    }
}