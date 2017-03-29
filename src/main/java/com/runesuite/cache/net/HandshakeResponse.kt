package com.runesuite.cache.net

import io.netty.buffer.ByteBuf

data class HandshakeResponse(override val byteBuf: ByteBuf) : Response(byteBuf) {

    val status: Status

    init {
        val sliced = byteBuf.slice()
        status = if (sliced.readableBytes() != 1) {
            Status.UNKNOWN
        } else {
            when (sliced.readByte()) {
                Status.SUCCESS.id -> Status.SUCCESS
                Status.OUTDATED.id -> Status.OUTDATED
                else -> Status.UNKNOWN
            }
        }
    }

    enum class Status(val id: Byte) {
        SUCCESS(0),
        OUTDATED(6),
        UNKNOWN(-1);
    }
}