package com.runesuite.cache.net

import io.netty.buffer.ByteBuf

internal data class ConnectionInfoOffer(val state: State) : Request() {

    override fun write(output: ByteBuf) {
        output.writeByte(state.id).writeMedium(0)
    }

    override fun toString(): String {
        return "ConnectionInfoOffer(state=$state)"
    }

    enum class State(val id: Int) {
        LOGGED_IN(2),
        LOGGED_OUT(3)
    }
}