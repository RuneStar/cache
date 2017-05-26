package com.runesuite.cache.format

import mu.KotlinLogging

abstract class WritableCache : ReadableCache() {

    private companion object {
        val logger = KotlinLogging.logger {  }
    }

    abstract fun setVolume(index: Int, archive: Int, data: Volume)

    abstract fun setIndexReference(index: Int, indexReference: IndexReference)

    fun updateReferences(master: ReadableCache) {
        val reference1 = master.getReference()
        val reference2 = getReference()
        reference1.indexReferences.forEachIndexed { i, indexRefInfo1 ->
            val indexRefInfo2 = reference2.indexReferences.getOrNull(i)
            if (indexRefInfo2 != null && indexRefInfo1 == indexRefInfo2) {
                logger.debug { "Index reference $i up to date" }
            } else {
                logger.debug { "Index reference $i out of date, updating" }
                val indexReference = master.getIndexReference(i)
                check(indexReference.volume.crc == indexRefInfo1.crc)
                setIndexReference(i, indexReference)
            }
        }
    }
}