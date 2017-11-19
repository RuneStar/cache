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

    private val indexRefs = HashMap<Int, CompletableFuture<IndexReference>>()

    override fun getIndexReference(index: Int): CompletableFuture<IndexReference> {
        return indexRefs.getOrPut(index) { local.getIndexReference(index) }
    }

    private lateinit var ref: CompletableFuture<StoreReference>

    override fun getReference(): CompletableFuture<StoreReference> {
        return ref
    }

    override fun getVolume(index: Int, volume: Int): CompletableFuture<out Volume?> {
        val ref = getIndexReference(index).join()
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
        ref = refMasterFuture
        val refLocal = local.getReference().join()
        val refMaster = refMasterFuture.join()
        refMaster.indexReferences.forEachIndexed { i, iriMaster ->
            val iriLocal = refLocal.indexReferences.getOrNull(i)
            if (iriLocal == null || iriLocal != iriMaster) {
                val irMasterFuture = master.getIndexReference(i)
                indexRefs[i] = irMasterFuture
                val irMaster = irMasterFuture.join()
                check(irMaster.volume.crc == iriMaster.crc)
                local.setIndexReference(i, irMaster)
            }
        }
        local.setReference(refMaster)
    }
}