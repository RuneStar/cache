package com.runesuite.cache.format

import mu.KotlinLogging

abstract class WritableCache : ReadableCache() {

    private companion object {
        val logger = KotlinLogging.logger {  }
    }

    abstract fun setContainer(index: Int, archive: Int, data: Container)

    abstract fun setIndexReference(index: Int, indexReference: IndexReference)

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
                setIndexReference(index, indexReference)
            }
        }
    }
}