package com.runesuite.cache.net

import com.runesuite.cache.buffer.asList
import io.netty.buffer.ByteBuf

class HandshakeResponse(override val byteBuf: ByteBuf) : Response(byteBuf) {

    val status: Status get() {
        return if (byteBuf.readableBytes() != 1) {
            Status.UNKNOWN
        } else {
            when (byteBuf.getByte(0)) {
                Status.SUCCESS.id -> Status.SUCCESS
                Status.INCORRECT_REVISION.id -> Status.INCORRECT_REVISION
                else -> Status.UNKNOWN
            }
        }
    }

    override fun toString(): String {
        return "HandshakeResponse(status=$status, byteBuf=${byteBuf.asList()})"
    }

    enum class Status(val id: Byte) {
        SUCCESS(0),
        INCORRECT_REVISION(6),
        UNKNOWN(-1);
    }
}