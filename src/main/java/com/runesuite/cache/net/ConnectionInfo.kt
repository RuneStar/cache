package com.runesuite.cache.net

import com.runesuite.cache.buffer.asList

class ConnectionInfo(val state: State) : Request() {

    override val byteBuf = io.netty.buffer.Unpooled.buffer(4).writeByte(state.id).writeMedium(0)

    override fun toString(): String {
        return "ConnectionInfo(state=$state, byteBuf=${byteBuf.asList()})"
    }

    enum class State(val id: Int) {
        LOGGED_IN(2),
        LOGGED_OUT(3)
    }
}