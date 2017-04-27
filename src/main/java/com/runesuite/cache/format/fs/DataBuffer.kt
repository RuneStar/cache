package com.runesuite.cache.format.fs

import com.runesuite.cache.extensions.readSliceMax
import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf
import io.netty.buffer.Unpooled

internal class DataBuffer(val buffer: ByteBuf) {

    fun get(archive: Int, indexEntry: IndexBuffer.Entry): CompositeByteBuf {
        val fullData = Unpooled.compositeBuffer()
        val view = buffer.slice()
        var currentSectorId = indexEntry.sector
        var currentChunk = 0
        var currentSector: Sector
        while (fullData.readableBytes() < indexEntry.length) {
            view.readerIndex(currentSectorId * Sector.LENGTH)
            currentSector = Sector.read(archive, view)
            currentSectorId = currentSector.nextSector
            check(currentChunk == currentSector.chunk)
            currentChunk++
            fullData.addComponent(true, currentSector.data.retain())
        }
        return fullData.writerIndex(indexEntry.length)
    }

    fun append(index: Int, archive: Int, data: ByteBuf) {
        var currentChunk = 0
        val view = data.slice()
        var currentSectorId = sectorCount
        val dataLength = Sector.LENGTH - Sector.headerLength(archive)
        while (view.isReadable) {
            val currentData = view.readSliceMax(dataLength)
            val nextSector = if (view.isReadable) currentSectorId + 1 else 0
            val currentSector = Sector(index, archive, currentChunk, nextSector, currentData)
            currentSector.write(buffer)
            currentSectorId++
            currentChunk++
        }
    }

    val sectorCount: Int get() {
        return buffer.readableBytes() / Sector.LENGTH
    }

    class Sector(val index: Int, val archive: Int, val chunk: Int, val nextSector: Int, val data: ByteBuf) {

        companion object {
            const val LENGTH = 520

            fun headerLength(archive: Int): Int {
                return if (archive < 0xFFFF) {
                    java.lang.Short.BYTES
                } else {
                    Integer.BYTES
                } + java.lang.Short.BYTES + 3 + java.lang.Byte.BYTES
            }

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
                return Sector(index, archive2, chunk, nextSector, data)
            }
        }

        fun write(buffer: ByteBuf) {
            val startPos = buffer.writerIndex()
            if (archive < 0xFFFF) {
                buffer.writeShort(archive)
            } else {
                buffer.writeInt(archive)
            }
            buffer.writeShort(chunk)
            buffer.writeMedium(nextSector)
            buffer.writeByte(index)
            buffer.writeBytes(data)
            buffer.writerIndex(startPos + LENGTH)
        }

        override fun toString(): String {
            return "Sector(index=$index, archive=$archive, chunk=$chunk, nextSector=$nextSector)"
        }
    }
}