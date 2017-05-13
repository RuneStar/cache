package com.runesuite.cache.format.fs

import io.netty.buffer.ByteBuf

internal class IndexBuffer(val buffer: ByteBuf) {

    fun get(archive: Int): Entry? {
        buffer.markReaderIndex()
        val view = buffer.readerIndex(archive * Entry.LENGTH)
        val entry = Entry.read(view)
        buffer.resetReaderIndex()
        return if (entry.length == 0 && entry.sector == 0) {
            null
        } else {
            entry
        }
    }

    fun set(archive: Int, entry: Entry) {
        val writerPos = buffer.writerIndex()
        buffer.writerIndex(archive * Entry.LENGTH)
        entry.write(buffer)
        buffer.writerIndex(Math.max(writerPos, buffer.writerIndex()))
    }

    val entryCount: Int get() = buffer.readableBytes() / Entry.LENGTH

    data class Entry(val length: Int, val sector: Int) {

        companion object {
            const val LENGTH = 3 + 3

            fun read(buffer: ByteBuf): Entry {
                val length = buffer.readUnsignedMedium()
                val sector = buffer.readUnsignedMedium()
                return Entry(length, sector)
            }
        }

        fun write(buffer: ByteBuf) {
            buffer.writeMedium(length)
            buffer.writeMedium(sector)
        }
    }
}