package com.runesuite.cache.format

import java.util.concurrent.CompletableFuture

class BackedStore(
        val local: WritableStore,
        val master: ReadableStore
) : ReadableStore {

    init {
        updateReferences()
    }

    override fun getIndexReference(index: Int): CompletableFuture<IndexReference> {
        return local.getIndexReference(index)
    }

    override fun getReference(): CompletableFuture<StoreReference> {
        return local.getReference()
    }

    override fun getVolume(index: Int, volume: Int): CompletableFuture<out Volume?> {
        val ref = local.getIndexReference(index).join()
        val archiveInfo = ref.archives.getOrNull(volume) ?: return CompletableFuture.completedFuture(null)
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
        val refMasterFuture = master.getReference()
        val refLocal = local.getReference().join()
        val refMaster = refMasterFuture.join()
        refMaster.indexReferences.forEachIndexed { i, iriMaster ->
            val iriLocal = refLocal.indexReferences.getOrNull(i)
            if (iriLocal == null || iriLocal != iriMaster) {
                val irMaster = master.getIndexReference(i).join()
                check(irMaster.volume.crc == iriMaster.crc)
                local.setIndexReference(i, irMaster)
            }
        }
        local.setReference(refMaster)
    }
}