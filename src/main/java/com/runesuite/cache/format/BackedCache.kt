package com.runesuite.cache.format

import com.runesuite.cache.extensions.closeQuietly
import com.runesuite.cache.format.fs.FileSystemCache
import com.runesuite.cache.format.net.NetClientCache
import mu.KotlinLogging
import java.io.IOException

class BackedCache(val local: WritableCache, val master: ReadableCache) : ReadableCache() {

    private var isOpen = true

    override fun isOpen() = isOpen

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
            local.updateReferences(master)
        } catch (e: Exception) {
            closeQuietly()
            throw e
        }
    }

    override val indices: Int
        get() = local.indices

    override fun getReference(): CacheReference {
        return local.getReference()
    }

    override fun getContainer(index: Int, archive: Int): Container? {
        val ref = getIndexReference(index)
        val archiveInfo = ref.archives[archive] ?: return null
        check(archiveInfo.id == archive)
        val localArchive = local.getContainer(index, archive)
        if (localArchive != null) {
            if (localArchive.crc == archiveInfo.crc) {
                logger.debug { "Archive found, up to date: $index, $archive" }
                return localArchive
            } else {
                logger.debug { "Archive found, out of date: $index, $archive. Expected crc: ${archiveInfo.crc}, found crc: ${localArchive.crc}" }
            }
        } else {
            logger.debug { "Archive not found: $index, $archive" }
        }
        logger.debug { "Fetching archive: $index, $archive" }
        val masterArchive = checkNotNull(master.getContainer(index, archive))
        check(masterArchive.crc == archiveInfo.crc)
        local.setContainer(index, archive, masterArchive)
        return masterArchive
    }

    override fun getIndexReference(index: Int): IndexReference {
        return local.getIndexReference(index)
    }

    override fun close() {
        if (isOpen) {
            isOpen = false
            master.closeQuietly()
            local.close()
        }
    }
}