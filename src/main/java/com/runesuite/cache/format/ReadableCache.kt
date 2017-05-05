package com.runesuite.cache.format

import java.io.Closeable

interface ReadableCache : Closeable {

    val indices: Int get() {
        return getReference().indexReferences.size
    }

    fun getReference(): CacheReference {
        val refEntries = (0 until indices).map {
            val indexRef = getIndexReference(it)
            CacheReference.IndexReferenceInfo(indexRef.archive.crc, indexRef.version)
        }
        return CacheReference(refEntries)
    }

    fun getIndexReference(index: Int): IndexReference

    fun getArchive(index: Int, archive: Int): Archive?
}