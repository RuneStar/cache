package com.runesuite.cache.format.net

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

data class FileRequest(val index: Int, val archive: Int) {

    class Encoder : MessageToByteEncoder<FileRequest>() {

        override fun encode(ctx: ChannelHandlerContext, msg: FileRequest, out: ByteBuf) {
            out.writeByte(if (msg.index == 255) 1 else 0)
            out.writeByte(msg.index)
            out.writeShort(msg.archive)
        }
    }
}