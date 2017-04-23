package com.runesuite.cache

import com.runesuite.cache.fs.FileSystemCache
import com.runesuite.cache.net.NetClientCache
import mu.KotlinLogging

open class BackedCache(val local: WritableCache, val master: ReadableCache) : WritableCache by local {

    class Default : BackedCache(FileSystemCache.Default(), NetClientCache.Default())

    private val logger = KotlinLogging.logger {  }

    init {
        local.updateReferenceTables(master)
    }

    override fun getArchiveCompressed(archiveId: ArchiveId): CompressedFile {
        val ref = getReferenceTable(archiveId.index)
        val entry = ref.entries[archiveId.archive]
        check(entry.id == archiveId.archive)
        val localCompressed = local.getArchiveCompressed(archiveId)
        if (localCompressed != null) {
            if (localCompressed.crc == entry.crc) {
                logger.debug { "Archive found, up to date: $archiveId" }
                return localCompressed
            } else {
                logger.debug { "Archive found, out of date: $archiveId. Expected crc: ${entry.crc}, found crc: ${localCompressed.crc}" }
            }
        } else {
            logger.debug { "Archive not found: $archiveId" }
        }
        logger.debug { "Fetching archive: $archiveId" }
        val remoteCompressed = checkNotNull(master.getArchiveCompressed(archiveId))
        check(remoteCompressed.crc == entry.crc)
        local.putArchiveCompressed(archiveId, remoteCompressed)
        return remoteCompressed
    }

    final override fun close() {
        local.close()
        master.close()
    }
}