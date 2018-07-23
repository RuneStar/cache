package org.runestar.cache.format

import java.nio.channels.Channel
import java.util.concurrent.CompletableFuture

class ReadableCache(
        val store: ReadableStore,
        dictionary: Set<String> = emptySet()
) : Channel by store {

    private val dictionaryMap = dictionary.associateBy { it.hashCode() }

    private val xteaCipher = XteaCipher()

    fun getIndexCount(): Int {
        return store.getReference().join().indexReferences.size
    }

    fun getArchiveCount(index: Int): Int {
        return store.getIndexReference(index).join().archives.size
    }

    fun getArchiveIdentifiers(index: Int): List<ArchiveIdentifier?> {
        return store.getIndexReference(index).join().archives.map {
            it?.let { ArchiveIdentifier(it.id, it.nameHash, dictionaryMap[it.nameHash]) }
        }
    }

    fun getArchive(index: Int, archive: Int, xteaKey: IntArray? = null): CompletableFuture<Archive?> {
        val volumeFuture = store.getVolume(index, archive)
        val indexReferenceFuture = store.getIndexReference(index)
        return volumeFuture.thenCombine(indexReferenceFuture) { v, ir ->
            val archiveInfo = ir.archives[archive] ?: return@thenCombine null
            val decompressed = v?.decompressed ?: return@thenCombine  null
            val data = xteaCipher.decrypt(decompressed, xteaKey)
            val name = dictionaryMap[archiveInfo.nameHash]
            Archive(ArchiveIdentifier(archiveInfo.id, archiveInfo.nameHash, name), data, archiveInfo.records.size)
        }
    }

    fun getArchive(index: Int, archiveName: String, xteaKey: IntArray? = null): CompletableFuture<Archive?> {
        val archiveNameHash = archiveName.hashCode()
        val indexReferenceFuture = store.getIndexReference(index)
        return indexReferenceFuture.thenApply { ir ->
            val archiveInfo = ir.archives.firstOrNull { it != null && it.nameHash == archiveNameHash } ?: return@thenApply null
            val volume = store.getVolume(index, archiveInfo.id).join() ?: return@thenApply null
            val data = xteaCipher.decrypt(volume.decompressed, xteaKey)
            val name = dictionaryMap[archiveInfo.nameHash] ?: archiveName
            Archive(ArchiveIdentifier(archiveInfo.id, archiveInfo.nameHash, name), data, archiveInfo.records.size)
        }
    }
}