package com.runesuite.cache

import io.netty.buffer.ByteBuf

class IndexBuffer(val buffer: ByteBuf) {

    fun read(id: Int): Entry {
        val view = buffer.slice().readerIndex(id * Entry.LENGTH)
        return Entry.read(view)
    }

    fun write(id: Int, indexEntry: Entry) {
        val view = buffer.slice().readerIndex(id * Entry.LENGTH)
        indexEntry.write(view)
    }

    val entryCount: Int = buffer.readableBytes() / Entry.LENGTH

    fun readAll(): List<Entry> {
        return (0 until entryCount).map {
            read(it)
        }
    }

    data class Entry(val sector: Int, val length: Int) {

        companion object {
            const val LENGTH = 6

            fun read(buffer: ByteBuf): Entry {
                val length = buffer.readMedium()
                val sector = buffer.readMedium()
                return Entry(length, sector)
            }
        }

        fun write(buffer: ByteBuf) {
            buffer.writeMedium(length)
            buffer.writeMedium(sector)
        }
    }
}