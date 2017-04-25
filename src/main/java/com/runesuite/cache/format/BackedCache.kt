package com.runesuite.cache.format

import com.runesuite.cache.extensions.closeQuietly
import com.runesuite.cache.format.fs.FileSystemCache
import com.runesuite.cache.format.net.NetClientCache
import mu.KotlinLogging

open class BackedCache(val local: WritableCache, val master: ReadableCache) : WritableCache by local {

    class Default : BackedCache(FileSystemCache.Default(), NetClientCache.Default())

    private val logger = KotlinLogging.logger {  }

    init {
        local.updateIndexReferences(master)
    }

    override fun getArchive(archiveId: ArchiveId): Archive {
        val ref = getIndexReference(archiveId.index)
        val entry = checkNotNull(ref.archives[archiveId.archive])
        check(entry.id == archiveId.archive)
        val localCompressed = local.getArchive(archiveId)
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
        val remoteCompressed = checkNotNull(master.getArchive(archiveId))
        check(remoteCompressed.crc == entry.crc)
        local.putArchive(archiveId, remoteCompressed)
        return remoteCompressed
    }

    final override fun close() {
        master.closeQuietly()
        local.close()
    }
}