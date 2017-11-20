package com.runesuite.cache.format

import java.io.IOException
import java.util.concurrent.CompletableFuture

class BackedStore(
        val local: WritableStore,
        val master: ReadableStore
) : ReadableStore {

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

    private val indexRefFutures = HashMap<Int, CompletableFuture<IndexReference>>()

    override fun getIndexReference(index: Int): CompletableFuture<IndexReference> {
        return indexRefFutures.getOrPut(index) { local.getIndexReference(index) }
    }

    private lateinit var refFuture: CompletableFuture<StoreReference>

    override fun getReference(): CompletableFuture<StoreReference> {
        return refFuture
    }

    override fun getVolume(index: Int, volume: Int): CompletableFuture<out Volume?> {
        val indexRef = getIndexReference(index).join()
        val archiveInfo = indexRef.archives.getOrNull(volume) ?: return CompletableFuture.completedFuture(null)
        check(archiveInfo.id == volume)
        val localVolume = local.getVolume(index, volume).join()
        if (localVolume != null && localVolume.crc == archiveInfo.crc) {
            return CompletableFuture.completedFuture(localVolume)
        }
        val masterVolume = master.getVolume(index, volume)
        masterVolume.thenAccept {
            val vol = checkNotNull(it)
            check(vol.crc == archiveInfo.crc)
            local.setVolume(index, volume, vol)
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
}