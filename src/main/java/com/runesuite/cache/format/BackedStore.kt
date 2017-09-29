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

    override fun getReference(): CompletableFuture<CacheReference> {
        return local.getReference()
    }

    override fun getVolume(index: Int, archive: Int): CompletableFuture<Volume> {
        TODO("not implemented")
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