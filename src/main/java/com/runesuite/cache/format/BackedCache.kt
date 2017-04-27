package com.runesuite.cache.format

import com.runesuite.cache.extensions.closeQuietly
import com.runesuite.cache.format.fs.FileSystemCache
import com.runesuite.cache.format.net.NetClientCache
import mu.KotlinLogging
import java.io.IOException

class BackedCache(val local: WritableCache, val master: ReadableCache) : WritableCache by local {

    companion object {
        @Throws(IOException::class)
        fun default(): BackedCache {
            val fs = FileSystemCache.default()
            val net: ReadableCache
            try {
                net = NetClientCache.default()
            } catch (newNetException: IOException) {
                fs.closeQuietly()
                throw newNetException
            }
            return BackedCache(fs, net)
        }
    }

    private val logger = KotlinLogging.logger {  }

    init {
        try {
            local.updateIndexReferences(master)
        } catch (e: Exception) {
            closeQuietly()
            throw e
        }
    }

    override fun getArchive(index: Int, archive: Int): Archive {
        val ref = getIndexReference(index)
        val entry = checkNotNull(ref.archives[archive])
        check(entry.id == archive)
        val localCompressed = local.getArchive(index, archive)
        if (localCompressed != null) {
            if (localCompressed.crc == entry.crc) {
                logger.debug { "Archive found, up to date: $index, $archive" }
                return localCompressed
            } else {
                logger.debug { "Archive found, out of date: $index, $archive. Expected crc: ${entry.crc}, found crc: ${localCompressed.crc}" }
            }
        } else {
            logger.debug { "Archive not found: $index, $archive" }
        }
        logger.debug { "Fetching archive: $index, $archive" }
        val remoteCompressed = checkNotNull(master.getArchive(index, archive))
        check(remoteCompressed.crc == entry.crc)
        local.putArchive(index, archive, remoteCompressed)
        return remoteCompressed
    }

    override fun close() {
        master.closeQuietly()
        local.close()
    }
}