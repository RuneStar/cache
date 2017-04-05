
package com.runesuite.cache

import io.netty.buffer.ByteBuf

class ReferenceTable(val buffer: ByteBuf) {

    fun readAll(): List<Entry> {
        val view = buffer.slice()
        return (0 until view.readableBytes() / 8).map {
            Entry(view.readInt(), view.readInt())
        }
    }

    data class Entry(val crc: Int, val version: Int)
}