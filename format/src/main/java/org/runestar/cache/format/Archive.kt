package org.runestar.cache.format

import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf
import io.netty.buffer.Unpooled

class Archive(
        val nameHash: Int?,
        val crc: Int,
        val version: Int?,
        val recordIds: IntArray,
        val records: List<Record?>
) {

    companion object {

        fun read(archiveInfo: IndexReference.ArchiveInfo, buffer: ByteBuf): Archive {
            val size = archiveInfo.recordIds.size
            val records = arrayOfNulls<Record?>(archiveInfo.records.size)
            if (size == 1) {
                val recordInfo = checkNotNull(archiveInfo.records.first())
                records[archiveInfo.recordIds.first()] = Record(buffer.retainedDuplicate(), recordInfo.nameHash)
            } else {
                val chunks = buffer.getUnsignedByte(buffer.writerIndex() - 1).toInt()
                // check(chunks == 1) { chunks } // todo
                buffer.markReaderIndex()
                buffer.readerIndex(buffer.writerIndex() - 1 - chunks * size * Integer.BYTES)
                val chunkSizes = Array(chunks) { IntArray(size) }
                for (chunk in 0 until chunks) {
                    var chunkSize = 0
                    for (r in 0 until size) {
                        chunkSize += buffer.readInt()
                        chunkSizes[chunk][r] = chunkSize
                    }
                }
                buffer.resetReaderIndex()
                buffer.markReaderIndex()
                for (r in archiveInfo.recordIds) {
                    val recordInfo = checkNotNull(archiveInfo.records[r])
                    records[r] = Record(Unpooled.compositeBuffer(chunks), recordInfo.nameHash)
                }
                for (chunk in 0 until chunks) {
                    for (r in 0 until size) {
                        val chunkSize = chunkSizes[chunk][r]
                        val buf = checkNotNull(records[archiveInfo.recordIds[r]]).buffer as CompositeByteBuf
                        buf.addComponent(true, buffer.readRetainedSlice(chunkSize))
                    }
                }
                buffer.resetReaderIndex()
            }
            return Archive(archiveInfo.nameHash, archiveInfo.crc, archiveInfo.version, archiveInfo.recordIds, records.asList())
        }
    }
}