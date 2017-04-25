package com.runesuite.cache.format.fs

import com.runesuite.cache.extensions.closeQuietly
import com.runesuite.cache.format.Archive
import com.runesuite.cache.format.ArchiveId
import com.runesuite.cache.format.WritableCache
import mu.KotlinLogging
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

open class FileSystemCache
@Throws(IOException::class)
constructor(val folder: Path) : WritableCache {

    class Default : FileSystemCache((Paths.get(System.getProperty("user.home"), "jagexcache", "oldschool", "LIVE")))

    private companion object {
        val MAIN_FILE_CACHE_DAT = "main_file_cache.dat2"
        val MAIN_FILE_CACHE_IDX = "main_file_cache.idx"
        const val REFERENCE_INDEX = 255

        const val MAX_INDEX_FILE_SIZE = 50_000
        const val MAX_REFERENCE_FILE_SIZE = 1_000_000
        const val MAX_DATA_FILE_SIZE = 200_000_000
    }

    private val logger = KotlinLogging.logger {  }

    private val dataFile: BufFile
    private val dataBuffer: DataBuffer

    private val referenceFile: BufFile
    private val referenceBuffer: IndexBuffer

    private val indexFiles: MutableCollection<BufFile> = ArrayList()
    private val indexBuffers: TreeMap<Int, IndexBuffer> = TreeMap()

    init {
        dataFile = BufFile(folder.resolve(MAIN_FILE_CACHE_DAT), MAX_DATA_FILE_SIZE)
        try {
            referenceFile = BufFile(folder.resolve("$MAIN_FILE_CACHE_IDX$REFERENCE_INDEX"), MAX_REFERENCE_FILE_SIZE)
        } catch (newReferenceFileException: IOException) {
            dataFile.closeQuietly()
            throw newReferenceFileException
        }
        dataBuffer = DataBuffer(dataFile.buffer)
        referenceBuffer = IndexBuffer(referenceFile.buffer)
    }

    override val indices: Int get() = referenceBuffer.entryCount

    @Throws(IOException::class)
    private fun loadIndex(index: Int) {
        logger.debug { "Loading index file: $index" }
        val file: BufFile
        try {
            file = BufFile(folder.resolve("$MAIN_FILE_CACHE_IDX$index"), MAX_INDEX_FILE_SIZE)
        } catch (newIndexFileException: IOException) {
            closeQuietly()
            throw newIndexFileException
        }
        indexFiles.add(file)
        val buf = IndexBuffer(file.buffer)
        indexBuffers.put(index, buf)
    }

    override fun getArchive(archiveId: ArchiveId): Archive? {
        val idx = archiveId.index
        if (!indexBuffers.containsKey(idx)) {
            loadIndex(idx)
        }
        val idxBuffer = checkNotNull(indexBuffers[idx])
        val archive = archiveId.archive
        if (archive >= idxBuffer.entryCount) {
            return null
        }
        val idxEntry = idxBuffer.get(archive) ?: return null
        return Archive(dataBuffer.get(archive, idxEntry))
    }

    override fun getIndexReferenceArchive(index: Int): Archive {
        return Archive(dataBuffer.get(index, checkNotNull(referenceBuffer.get(index))))
    }

    override fun putIndexReferenceArchive(index: Int, archive: Archive) {
        putArchive(ArchiveId(REFERENCE_INDEX, index), referenceBuffer, archive)
    }

    override fun putArchive(archiveId: ArchiveId, archive: Archive) {
        val idx = archiveId.index
        if (!indexBuffers.containsKey(idx)) {
            loadIndex(idx)
        }
        val idxBuffer = checkNotNull(indexBuffers[idx])
        putArchive(archiveId, idxBuffer, archive)
    }

    private fun putArchive(archiveId: ArchiveId, indexBuffer: IndexBuffer, archive: Archive) {
        val data = archive.buffer
        val length = archive.compressedDataLength + Archive.HEADER_LENGTH
        val sector = dataBuffer.sectorCount
        dataBuffer.append(archiveId, data)
        val indexEntry = IndexBuffer.Entry(length, sector)
        indexBuffer.put(archiveId.archive, indexEntry)
    }

    override fun close() {
        indexFiles.forEach { it.closeQuietly() }
        referenceFile.closeQuietly()
        dataFile.close()
    }
}