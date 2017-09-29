package com.runesuite.cache.format

import io.netty.buffer.ByteBuf

data class CacheReference(val indexReferences: List<IndexReferenceInfo>) {

    companion object {

        @JvmStatic
        fun read(buffer: ByteBuf): CacheReference {
            val entries = ArrayList<IndexReferenceInfo>(buffer.readableBytes() / IndexReferenceInfo.LENGTH)
            while(buffer.isReadable) {
                entries.add(IndexReferenceInfo.read(buffer))
            }
            return CacheReference(entries)
        }
    }

    data class IndexReferenceInfo(val crc: Int, val version: Int) {

        companion object {
            const val LENGTH = Integer.BYTES * 2

            @JvmStatic
            fun read(buffer: ByteBuf): IndexReferenceInfo {
                return IndexReferenceInfo(buffer.readInt(), buffer.readInt())
            }
        }
    }
}