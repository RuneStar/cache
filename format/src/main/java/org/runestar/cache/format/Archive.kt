package org.runestar.cache.format

import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf
import io.netty.buffer.Unpooled

class Archive(
        val identifier: ArchiveIdentifier,
        buffer: ByteBuf,
        size: Int
) {

    val records: List<ByteBuf>

    init {
        require(size > 0)
        records = if (size == 1) {
            listOf(buffer.retainedDuplicate())
        } else {
            val chunks = buffer.getUnsignedByte(buffer.writerIndex() - 1).toInt()
            buffer.markReaderIndex()
            buffer.readerIndex(buffer.writerIndex() - 1 - chunks * size * Integer.BYTES)
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
            buffer.markReaderIndex()
            val entries = MutableList<CompositeByteBuf>(size) { Unpooled.compositeBuffer(chunks) }
            for (chunk in 0 until chunks) {
                for (file in 0 until size) {
                    val chunkSize = chunkSizes[chunk][file]
                    entries[file].addComponent(true, buffer.readRetainedSlice(chunkSize))
                }
            }
            buffer.resetReaderIndex()
            entries
        }
    }

    override fun toString(): String {
        return "Archive(records.size=${records.size}, identifier=$identifier)"
    }
}