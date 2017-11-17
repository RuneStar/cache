package com.runesuite.cache.format

import java.nio.channels.Channel

class ReadableCache(val store: ReadableStore) : Channel by store {

    val xteaKeys: MutableMap<Pair<Int, Int>, IntArray> = HashMap()

    private val xteaCipher = XteaCipher()

    fun getIndexCount(): Int {
        return store.getReference().join().indexReferences.size
    }

    fun getArchiveCount(index: Int): Int {
        return store.getIndexReference(index).join().archives.size
    }

    fun getArchiveNameHashes(index: Int): List<Int?> {
        return store.getIndexReference(index).join().archives.map { it?.nameHash }
    }

    fun getArchives(index: Int): List<Archive?> {
        val indexRef = store.getIndexReference(index).join()
        val archiveCount = indexRef.archives.size
        val archiveFutures = Array(archiveCount) { archiveIndex ->
            val key = xteaKeys[index to archiveIndex]
            store.getVolume(index, archiveIndex).thenApply { volume ->
                val archiveInfo = indexRef.archives[archiveIndex]
                if (volume == null || archiveInfo == null) {
                    null
                } else {
                    Archive(xteaCipher.decrypt(volume.decompressed, key), archiveInfo.records.size)
                }
            }
        }
        return archiveFutures.map { it.join() }
    }

    fun getArchive(index: Int, archive: Int): Archive? {
        return getArchive0(index, archive, store.getIndexReference(index).join())
    }

    fun getArchive(index: Int, archiveName: String): Archive? {
        val indexRef = store.getIndexReference(index).join()
        val nameHash = archiveName.hashCode()
        val archiveInfo = indexRef.archives.asSequence().filterNotNull().firstOrNull { it.nameHash == nameHash } ?: return null
        return getArchive0(index, archiveInfo.id, indexRef)
    }

    private fun getArchive0(index: Int, archive: Int, indexReference: IndexReference): Archive? {
        val volume = store.getVolume(index, archive).join() ?: return null
        val archiveInfo = indexReference.archives[archive] ?: return null
        val key = xteaKeys[index to archive]
        val data = xteaCipher.decrypt(volume.decompressed, key)
        return Archive(data, archiveInfo.records.size)
    }
}