package org.runestar.cache.format.net

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

internal data class HandshakeRequest(val byte: Byte, val revision: Int) {

    class Encoder : MessageToByteEncoder<HandshakeRequest>() {

        override fun encode(ctx: ChannelHandlerContext, msg: HandshakeRequest, out: ByteBuf) {
            out.writeByte(msg.byte.toInt())
            out.writeInt(msg.revision)
        }
    }
}