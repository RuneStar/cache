package com.runesuite.cache.format.net

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

internal data class ConnectionInfoOffer(val byte: Byte, val medium: Int) {

    class Encoder : MessageToByteEncoder<ConnectionInfoOffer>() {

        override fun encode(ctx: ChannelHandlerContext, msg: ConnectionInfoOffer, out: ByteBuf) {
            out.writeByte(msg.byte.toInt())
            out.writeMedium(msg.medium)
        }
    }
}