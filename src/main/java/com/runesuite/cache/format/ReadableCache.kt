package com.runesuite.cache.format

import java.nio.channels.Channel

abstract class ReadableCache : Channel {

    open val indices: Int get() {
        return getReference().indexReferences.size
    }

    open fun getReference(): CacheReference {
        val refEntries = (0 until indices).map {
            val indexRef = getIndexReference(it)
            CacheReference.IndexReferenceInfo(indexRef.container.crc, indexRef.version)
        }
        return CacheReference(refEntries)
    }

    abstract fun getIndexReference(index: Int): IndexReference

    abstract fun getContainer(index: Int, archive: Int): Container?

    fun getArchive(index: Int, archive: Int): Archive? {
        val container = getContainer(index, archive) ?: return null
        val size = getIndexReference(index).archives[archive]?.files?.size ?: return null
        val cipher = archiveSecrets[index, archive]
        val data = when (cipher) {
            null -> container.decompressed
            else -> cipher.decrypt(container.decompressed)
        }
        return Archive(data, size)
    }

    val archiveSecrets = ArchiveSecrets()
}