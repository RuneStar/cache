package com.runesuite.cache.format.net

import io.netty.buffer.ByteBuf

internal class HandshakeResponse(override val input: ByteBuf) : Response(input) {

    val status: Status get() {
        return if (input.readableBytes() != 1) {
            Status.UNKNOWN
        } else {
            when (input.getByte(0)) {
                Status.SUCCESS.id -> Status.SUCCESS
                Status.INCORRECT_REVISION.id -> Status.INCORRECT_REVISION
                else -> Status.UNKNOWN
            }
        }
    }

    override fun toString(): String {
        return "HandshakeResponse(status=$status)"
    }

    enum class Status(val id: Byte) {
        SUCCESS(0),
        INCORRECT_REVISION(6),
        UNKNOWN(-1);
    }
}