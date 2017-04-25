package com.runesuite.cache.format

import mu.KotlinLogging

interface WritableCache : ReadableCache {

    companion object {
        private val logger = KotlinLogging.logger {  }
    }

    fun putArchive(archiveId: ArchiveId, archive: Archive)

    fun putIndexReferenceArchive(index: Int, archive: Archive)

    fun updateIndexReferences(readableCache: ReadableCache) {
        val reference1 = readableCache.getReference()
        val reference2 = getReference()
        reference1.indexReferences.forEachIndexed { index, indexRef1 ->
            val indexRef2 = reference2.indexReferences.getOrNull(index)
            if (indexRef2 != null && indexRef1 == indexRef2) {
                logger.debug { "Index reference $index up to date" }
            } else {
                logger.debug { "Index reference $index out of date, updating" }
                val indexRefArchive = readableCache.getIndexReferenceArchive(index)
                check(indexRefArchive.crc == indexRef1.crc)
                putIndexReferenceArchive(index, indexRefArchive)
            }
        }
    }
}