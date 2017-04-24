package com.runesuite.cache.format

import io.netty.buffer.ByteBuf

data class ChecksumTable(val entries: List<Entry>) {

    companion object {

        fun read(buffer: ByteBuf): ChecksumTable {
            val entries = ArrayList<Entry>(buffer.readableBytes() / Entry.LENGTH)
            while(buffer.isReadable) {
                entries.add(Entry.read(buffer))
            }
            return ChecksumTable(entries)
        }
    }

    data class Entry(val crc: Int, val version: Int) {

        companion object {
            const val LENGTH = Integer.BYTES * 2

            fun read(buffer: ByteBuf): Entry {
                return Entry(buffer.readInt(), buffer.readInt())
            }
        }
    }
}