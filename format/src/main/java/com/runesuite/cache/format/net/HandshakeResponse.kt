package com.runesuite.cache.format.net

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

internal enum class HandshakeResponse(val id: Byte) {

    SUCCESS(0),
    INCORRECT_REVISION(6);

    companion object {
        val LOOKUP = values().associateBy { it.id }
    }

    class Decoder : ByteToMessageDecoder() {

        override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
            val byte = input.readByte()
            val msg = checkNotNull(HandshakeResponse.LOOKUP[byte]) { "unknown handshake response: $byte" }
            out.add(msg)
        }
    }
}