package com.runesuite.cache.net

class ConnectionInfo(val state: State) : Request() {

    override val byteBuf get() = io.netty.buffer.Unpooled.buffer(4).writeByte(state.id).writeMedium(0)

    enum class State(val id: Int) {
        LOGGED_IN(2),
        LOGGED_OUT(3)
    }
}