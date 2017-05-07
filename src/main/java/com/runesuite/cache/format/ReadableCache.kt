package com.runesuite.cache.format

import java.io.Closeable

interface ReadableCache : Closeable {

    val indices: Int get() {
        return getReference().indexReferences.size
    }

    fun getReference(): CacheReference {
        val refEntries = (0 until indices).map {
            val indexRef = getIndexReference(it)
            CacheReference.IndexReferenceInfo(indexRef.container.crc, indexRef.version)
        }
        return CacheReference(refEntries)
    }

    fun getIndexReference(index: Int): IndexReference

    fun getContainer(index: Int, archive: Int): Container?

    fun getArchive(index: Int, archive: Int): Archive? {
        val container = getContainer(index, archive) ?: return null
        val size = getIndexReference(index).archives[archive]?.files?.size ?: return null
        return Archive(container.decompressed, size)
    }
}