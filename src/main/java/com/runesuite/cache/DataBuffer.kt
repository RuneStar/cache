package com.runesuite.cache

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

class DataBuffer(val buffer: ByteBuf) {

    fun get(archive: Int, indexEntry: IndexBuffer.Entry): ByteBuf {
        val fullData = Unpooled.buffer(indexEntry.length)
        val view = buffer.slice()
        var currentSectorId = indexEntry.sector
        var currentChunk = 0
        var currentSector: Sector
        while (fullData.readableBytes() <= indexEntry.length) {
            view.readerIndex(currentSectorId * Sector.LENGTH)
            currentSector = Sector.read(archive, view.slice())
            currentSectorId = currentSector.nextSector
            check(currentChunk == currentSector.chunk)
            currentChunk++
            fullData.writeBytes(currentSector.data)
        }
        return fullData
    }

    class Sector(val archive: Int, val chunk: Int, val nextSector: Int, val index: Int, val data: ByteBuf) {

        companion object {
            const val LENGTH = 520

            fun read(archive: Int, buffer: ByteBuf): Sector {
                val startPos = buffer.readerIndex()
                val archive2 = if (archive < 0xFFFF) {
                    buffer.readUnsignedShort()
                } else {
                    buffer.readInt()
                }
                check(archive == archive2) { "actual archive $archive2 != expected archive $archive" }
                val chunk = buffer.readUnsignedShort()
                val nextSector = buffer.readMedium()
                val index = buffer.readUnsignedByte().toInt()
                val data = buffer.readSlice(LENGTH - (buffer.readerIndex() - startPos))
                return Sector(archive2, chunk, nextSector, index, data)
            }
        }

        fun write(buffer: ByteBuf) {
            if (archive < 0xFFFF) {
                buffer.writeShort(archive)
            } else {
                buffer.writeInt(archive)
            }
            buffer.writeShort(chunk)
            buffer.writeMedium(nextSector)
            buffer.writeByte(index)
            buffer.writeBytes(data)
        }

        override fun toString(): String {
            return "Sector(archive=$archive, chunk=$chunk, nextSector=$nextSector, index=$index)"
        }
    }
}