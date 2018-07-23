package org.runestar.cache.format

import io.netty.buffer.ByteBuf

data class StoreReference(val indexReferences: List<IndexReferenceInfo>) {

    companion object {

        fun read(buffer: ByteBuf): StoreReference {
            val entries = ArrayList<IndexReferenceInfo>(buffer.readableBytes() / IndexReferenceInfo.LENGTH)
            while(buffer.isReadable) {
                entries.add(IndexReferenceInfo.read(buffer))
            }
            return StoreReference(entries)
        }
    }

    data class IndexReferenceInfo(val crc: Int, val version: Int) {

        companion object {
            const val LENGTH = Integer.BYTES * 2

            fun read(buffer: ByteBuf): IndexReferenceInfo {
                return IndexReferenceInfo(buffer.readInt(), buffer.readInt())
            }
        }
    }
}