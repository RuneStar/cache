package com.runesuite.cache.format

import mu.KotlinLogging

interface WritableCache : ReadableCache {

    private companion object {
        val logger = KotlinLogging.logger {  }
    }

    fun putContainer(index: Int, archive: Int, data: Container)

    fun putIndexReference(index: Int, indexReference: IndexReference)

    fun updateReferences(readableCache: ReadableCache) {
        val reference1 = readableCache.getReference()
        val reference2 = getReference()
        reference1.indexReferences.forEachIndexed { index, indexRefInfo1 ->
            val indexRefInfo2 = reference2.indexReferences.getOrNull(index)
            if (indexRefInfo2 != null && indexRefInfo1 == indexRefInfo2) {
                logger.debug { "Index reference $index up to date" }
            } else {
                logger.debug { "Index reference $index out of date, updating" }
                val indexReference = readableCache.getIndexReference(index)
                check(indexReference.container.crc == indexRefInfo1.crc)
                putIndexReference(index, indexReference)
            }
        }
    }
}