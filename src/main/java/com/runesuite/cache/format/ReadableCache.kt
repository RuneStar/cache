package com.runesuite.cache.format

import java.nio.channels.Channel

abstract class ReadableCache : Channel {

    open val indices: Int get() {
        return getReference().indexReferences.size
    }

    open fun getReference(): CacheReference {
        val refEntries = (0 until indices).map {
            val indexRef = getIndexReference(it)
            CacheReference.IndexReferenceInfo(indexRef.volume.crc, indexRef.version)
        }
        return CacheReference(refEntries)
    }

    abstract fun getIndexReference(index: Int): IndexReference

    abstract fun getVolume(index: Int, archive: Int): Volume?

    fun getArchive(index: Int, archive: Int): Archive? {
        return getArchive(index, archive, getIndexReference(index))
    }

    private fun getArchive(index: Int, archive: Int, indexReference: IndexReference): Archive? {
        val volume = getVolume(index, archive) ?: return null
        val size = indexReference.archives[archive]?.files?.size ?: return null
        val cipher = archiveSecrets[index, archive]
        val data = when (cipher) {
            null -> volume.decompressed
            else -> cipher.decrypt(volume.decompressed)
        }
        return Archive(data, size)
    }

    fun getArchive(index: Int, archiveName: String): Archive? {
        val indexRef = getIndexReference(index)
        val nameHash = archiveName.hashCode()
        val archiveRef = indexRef.archives.asSequence()
                .filterNotNull()
                .firstOrNull { it.nameHash == nameHash } ?: return null
        return getArchive(index, archiveRef.id, indexRef)
    }

    val archiveSecrets = ArchiveSecrets()
}