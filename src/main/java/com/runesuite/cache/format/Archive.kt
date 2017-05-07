package com.runesuite.cache.format

import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf
import io.netty.buffer.PooledByteBufAllocator

class Archive(buffer: ByteBuf, size: Int) {

    val files: List<ByteBuf>

    init {
        val chunks = buffer.getUnsignedByte(buffer.capacity() - 1).toInt()
        val entries = Array<CompositeByteBuf>(size) { PooledByteBufAllocator.DEFAULT.compositeBuffer(chunks) }
        buffer.markReaderIndex()
        buffer.readerIndex(buffer.capacity() - 1 - chunks * size - Integer.BYTES)
        val chunkSizes = Array(chunks) { IntArray(size) }
        for (chunk in 0 until chunks) {
            var chunkSize = 0
            for (file in 0 until size) {
                val delta = buffer.readInt()
                chunkSize += delta
                chunkSizes[chunk][file] = chunkSize
            }
        }
        buffer.resetReaderIndex()
        for (chunk in 0 until chunks) {
            for (file in 0 until size) {
                val chunkSize = chunkSizes[chunk][file]
                entries[file].addComponent(true, buffer.readSlice(chunkSize))
            }
        }
        files = entries.asList()
    }
}