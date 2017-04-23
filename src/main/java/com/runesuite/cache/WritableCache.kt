package com.runesuite.cache

import mu.KotlinLogging

interface WritableCache : ReadableCache {

    companion object {
        private val logger = KotlinLogging.logger {  }
    }

    fun putArchiveCompressed(archiveId: ArchiveId, compressedFile: CompressedFile)

    fun putReferenceTableCompressed(index: Int, compressedFile: CompressedFile)

    fun updateReferenceTables(readableCache: ReadableCache) {
        val checksumTable1 = readableCache.getChecksumTable()
        val checksumTable2 = getChecksumTable()
        checksumTable1.entries.forEachIndexed { index, checksumEntry1 ->
            val checksumEntry2 = checksumTable2.entries.getOrNull(index)
            if (checksumEntry2 != null && checksumEntry1 == checksumEntry2) {
                logger.debug { "Reference table $index up to date" }
            } else {
                logger.debug { "Reference table $index out of date, updating" }
                val refCompressed1 = readableCache.getReferenceTableCompressed(index)
                check(refCompressed1.crc == checksumEntry1.crc)
                putReferenceTableCompressed(index, refCompressed1)
            }
        }
    }
}