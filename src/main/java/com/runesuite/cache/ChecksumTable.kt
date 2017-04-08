package com.runesuite.cache

import io.netty.buffer.ByteBuf

data class ChecksumTable(val entries: List<Entry>) {

    companion object {

        fun read(buffer: ByteBuf): ChecksumTable {
            val entries = ArrayList<Entry>(buffer.readableBytes() / Entry.LENGTH)
            while(buffer.isReadable) {
                entries.add(Entry(buffer.readInt(), buffer.readInt()))
            }
            return ChecksumTable(entries)
        }
    }

    data class Entry(val crc: Int, val version: Int) {

        companion object {
            const val LENGTH = 8
        }
    }
}