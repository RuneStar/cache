package org.runestar.cache.format.net

import org.runestar.cache.format.Compressor
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ReplayingDecoder

internal data class FileResponse(val index: Int, val volume: Int, val data: ByteBuf) {

    class Decoder : ReplayingDecoder<Void>() {

        override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
            val index = input.readUnsignedByte().toInt()
            val volume = input.readUnsignedShort()
            val compressorId = input.readByte()
            val compressor = checkNotNull(Compressor.LOOKUP[compressorId]) { "unknown compressor id: $compressorId" }
            val compressedLength = input.readInt() + compressor.headerLength
            input.readerIndex(input.readerIndex() - 5)
            val data = input.readRetainedSlice(compressedLength + 5)
            out.add(FileResponse(index, volume, data))
        }
    }

    // todo, faster
    class FrameDecoder : ReplayingDecoder<Void>() {

        private companion object {
            const val LENGTH = 512
            const val DELIMITER: Byte = -1
        }

        override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
            val compressorId = input.getByte(input.readerIndex() + 3)
            val compressor = checkNotNull(Compressor.LOOKUP[compressorId]) { "unknown compressor id: $compressorId" }
            val totalLength = input.getInt(input.readerIndex() + 4) + compressor.headerLength + 8
            var curOut = 0
            var curIn = 0
            val fullFile = ctx.alloc().compositeBuffer()
            while (curOut < totalLength) {
                if (curOut != 0 && curIn % LENGTH == 0) {
                    val b = input.readByte()
                    check(b == DELIMITER) { "wrong delimiter; expected $DELIMITER but got $b" }
                    curIn++
                } else {
                    val toRead = Math.min(totalLength - curOut, LENGTH - (curIn % LENGTH))
                    fullFile.addComponent(true, input.readRetainedSlice(toRead))
                    curOut += toRead
                    curIn += toRead
                }
            }
            out.add(fullFile)
        }
    }
}