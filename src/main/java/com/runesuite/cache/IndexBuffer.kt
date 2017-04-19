package com.runesuite.cache

import io.netty.buffer.ByteBuf

class IndexBuffer(val buffer: ByteBuf) {

    init {
        check(buffer.readableBytes() % Entry.LENGTH == 0)
    }

    fun get(archive: Int): Entry {
        val view = buffer.slice().readerIndex(archive * Entry.LENGTH)
        return Entry.read(view)
    }

    fun put(archive: Int, entry: Entry) {
        val view = buffer.slice().readerIndex(archive * Entry.LENGTH)
        entry.write(view)
    }

    val entryCount: Int = buffer.readableBytes() / Entry.LENGTH

    fun getAll(): List<Entry> {
        return (0 until entryCount).map {
            get(it)
        }
    }

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