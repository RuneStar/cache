package org.runestar.cache.format

import java.nio.channels.Channel
import java.util.concurrent.CompletableFuture

interface ReadableCache : Channel {

    fun getReference(): CompletableFuture<StoreReference>

    fun getIndexReference(index: Int): CompletableFuture<IndexReference>

    fun getVolume(index: Int, archive: Int): CompletableFuture<out Volume?>

    fun getIndexCount(): Int = getReference().join().indexReferences.size

    fun getArchiveIds(index: Int): IntArray = getIndexReference(index).join().archiveIds

    fun getArchive(index: Int, archive: Int, xteaKey: IntArray? = null): CompletableFuture<Archive?> {
        val volumeFuture = getVolume(index, archive)
        val indexReferenceFuture = getIndexReference(index)
        return volumeFuture.thenCombine(indexReferenceFuture) { v, ir ->
            val archiveInfo = ir.archives[archive] ?: return@thenCombine null
            val volume = v ?: return@thenCombine  null
            val data = volume.decompress(xteaKey)
            Archive.read(archiveInfo, data)
        }
    }

    fun getArchive(index: Int, archiveName: String, xteaKey: IntArray? = null): CompletableFuture<Archive?> {
        val indexReferenceFuture = getIndexReference(index)
        return indexReferenceFuture.thenApply { ir ->
            val archiveInfoId = ir.getArchiveId(archiveName) ?: return@thenApply null
            val archiveInfo = checkNotNull(ir.archives[archiveInfoId])
            val volume = getVolume(index, archiveInfoId).join() ?: return@thenApply null
            val data = volume.decompress(xteaKey)
            Archive.read(archiveInfo, data)
        }
    }
}