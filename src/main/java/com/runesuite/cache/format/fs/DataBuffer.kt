package com.runesuite.cache.format.fs

import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf
import io.netty.buffer.Unpooled

internal class DataBuffer(val buffer: ByteBuf) {

    fun get(volume: Int, indexEntry: IndexBuffer.Entry): CompositeByteBuf {
        val fullData = Unpooled.compositeBuffer()
        val view = buffer.duplicate()
        var currentSectorId = indexEntry.sector
        var currentChunk = 0
        var currentSector: Sector
        while (fullData.readableBytes() < indexEntry.length) {
            view.readerIndex(currentSectorId * Sector.LENGTH)
            currentSector = Sector.read(volume, view)
            currentSectorId = currentSector.nextSector
            check(currentChunk == currentSector.chunk)
            fullData.addComponent(true, currentSector.data.retain())
            currentChunk++
        }
        return fullData.writerIndex(indexEntry.length)
    }

    fun append(index: Int, volume: Int, data: ByteBuf) {
        var currentChunk = 0
        val view = data.duplicate()
        var currentSectorId = sectorCount
        val dataLength = Sector.LENGTH - Sector.headerLength(volume)
        while (view.isReadable) {
            val currentDataLength = Math.min(view.readableBytes(), dataLength)
            val currentData = view.readSlice(currentDataLength)
            val nextSector = if (view.isReadable) currentSectorId + 1 else 0
            val currentSector = Sector(index, volume, currentChunk, nextSector, currentData)
            currentSector.write(buffer)
            currentSectorId++
            currentChunk++
        }
    }

    val sectorCount: Int get() {
        return buffer.readableBytes() / Sector.LENGTH
    }

    class Sector(val index: Int, val volume: Int, val chunk: Int, val nextSector: Int, val data: ByteBuf) {

        companion object {
            const val LENGTH = 520

            fun headerLength(volume: Int): Int {
                return if (volume < 0xFFFF) {
                    java.lang.Short.BYTES
                } else {
                    Integer.BYTES
                } + java.lang.Short.BYTES + 3 + java.lang.Byte.BYTES
            }

            fun read(volume: Int, buffer: ByteBuf): Sector {
                val startPos = buffer.readerIndex()
                val volume2 = if (volume < 0xFFFF) {
                    buffer.readUnsignedShort()
                } else {
                    buffer.readInt()
                }
                check(volume == volume2) { "actual volume $volume2 != expected volume $volume" }
                val chunk = buffer.readUnsignedShort()
                val nextSector = buffer.readMedium()
                val index = buffer.readUnsignedByte().toInt()
                val data = buffer.readSlice(LENGTH - (buffer.readerIndex() - startPos))
                return Sector(index, volume2, chunk, nextSector, data)
            }
        }

        fun write(buffer: ByteBuf) {
            val startPos = buffer.writerIndex()
            if (volume < 0xFFFF) {
                buffer.writeShort(volume)
            } else {
                buffer.writeInt(volume)
            }
            buffer.writeShort(chunk)
            buffer.writeMedium(nextSector)
            buffer.writeByte(index)
            buffer.writeBytes(data)
            buffer.writerIndex(startPos + LENGTH)
        }

        override fun toString(): String {
            return "Sector(index=$index, volume=$volume, chunk=$chunk, nextSector=$nextSector)"
        }
    }
}