package com.runesuite.cache.format

import java.io.Closeable

interface ReadableCache : Closeable {

    val indices: Int get() {
        return getReference().indexReferences.size
    }

    fun getReference(): CacheReference {
        return createReference()
    }

    fun createReference(): CacheReference {
        val refEntries = (0 until indices).map {
            val archive = getIndexReferenceArchive(it)
            val indexRef = IndexReference.read(archive.data)
            CacheReference.IndexReferenceInfo(archive.crc, indexRef.version)
        }
        return CacheReference(refEntries)
    }

    fun getIndexReferenceArchive(index: Int): Archive

    fun getIndexReference(index: Int): IndexReference {
        return IndexReference.read(getIndexReferenceArchive(index).data)
    }

    fun getArchive(index: Int, archive: Int): Archive?
}