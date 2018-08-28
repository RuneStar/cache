package org.runestar.cache.format

import java.io.IOException
import java.util.concurrent.CompletableFuture

class BackedCache(
        val local: WritableCache,
        val master: ReadableCache
) : ReadableCache {

    private val indexRefFutures = HashMap<Int, CompletableFuture<IndexReference>>()

    init {
        updateReferences()
    }

    @Throws(IOException::class)
    override fun close() {
        master.use {
            local.close()
        }
    }

    override fun isOpen(): Boolean {
        return local.isOpen && master.isOpen
    }

    override fun getIndexReference(index: Int): CompletableFuture<IndexReference> {
        return indexRefFutures.getOrPut(index) { local.getIndexReference(index) }
    }

    private lateinit var refFuture: CompletableFuture<StoreReference>

    override fun getReference(): CompletableFuture<StoreReference> {
        return refFuture
    }

    override fun getVolume(index: Int, archive: Int): CompletableFuture<out Volume?> {
        val indexRef = getIndexReference(index).join()
        val archiveInfo = indexRef.archives.getOrNull(archive) ?: return CompletableFuture.completedFuture(null)
        val localVolume = local.getVolume(index, archive).join()
        if (localVolume != null && localVolume.crc == archiveInfo.crc) {
            return CompletableFuture.completedFuture(localVolume)
        }
        val masterVolume = master.getVolume(index, archive)
        masterVolume.thenAccept {
            val vol = checkNotNull(it)
            check(vol.crc == archiveInfo.crc)
            local.setVolume(index, archive, vol)
        }
        return masterVolume
    }

    private fun updateReferences() {
        refFuture = master.getReference()
        val refLocal = local.getReference().join()
        val refMaster = refFuture.join()
        refMaster.indexReferences.forEachIndexed { i, iriMaster ->
            val iriLocal = refLocal.indexReferences.getOrNull(i)
            if (iriLocal == null || iriLocal != iriMaster) {
                val irMasterFuture = master.getIndexReference(i)
                indexRefFutures[i] = irMasterFuture
                val irMaster = irMasterFuture.join()
                check(irMaster.volume.crc == iriMaster.crc)
                local.setIndexReference(i, irMaster)
            }
        }
        local.setReference(refMaster)
    }

    override fun toString(): String {
        return "BackedCache(local=$local, master=$master)"
    }
}