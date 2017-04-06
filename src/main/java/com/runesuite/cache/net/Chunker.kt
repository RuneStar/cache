package com.runesuite.cache.net

import com.runesuite.cache.extensions.readSliceMax
import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf

open class Chunker(val chunkLength: Int, val breakByte: Byte) {

    // todo
    private fun breaksCount(length: Int): Int {
        return if (length <= chunkLength) {
            0
        } else {
            Math.ceil((length - chunkLength).toDouble() / (chunkLength - 1).toDouble()).toInt()
        }
    }

    // todo
    private fun nextBreakAfter(length: Int): Int {
        return if (length <= chunkLength) {
            chunkLength - length
        } else {
            ((chunkLength - 1) - ((length - chunkLength) % (chunkLength - 1))) % (chunkLength - 1)
        }
    }

    fun join(buffer: CompositeByteBuf, input: ByteBuf) {
        while (input.isReadable) {
            val nextBreakIn = nextBreakAfter(buffer.readableBytes())
            val nextIsChunk = nextBreakIn == 0
            if (nextIsChunk) {
                check(input.readByte() == breakByte)
            }
            val chunk = input.readSliceMax(if (nextIsChunk) (chunkLength - 1) else nextBreakIn)
            buffer.addComponent(true, chunk)
        }
    }

    // todo: split

    object Default : Chunker(512, (-1).toByte())
}