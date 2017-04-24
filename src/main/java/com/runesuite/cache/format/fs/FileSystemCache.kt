package com.runesuite.cache.format.fs

import com.runesuite.cache.format.ArchiveId
import com.runesuite.cache.format.CompressedFile
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
        } catch (e: IOException) {
            dataFile.close()
            throw e
        }
        dataBuffer = DataBuffer(dataFile.buffer)
        referenceBuffer = IndexBuffer(referenceFile.buffer)
    }

    override val indexCount: Int get() = referenceBuffer.entryCount

    @Throws(IOException::class)
    private fun loadIndex(index: Int) {
        logger.debug { "Loading index file: $index" }
        val file: BufFile
        try {
            file = BufFile(folder.resolve("$MAIN_FILE_CACHE_IDX$index"), MAX_INDEX_FILE_SIZE)
        } catch (e: IOException) {
            close()
            throw e
        }
        indexFiles.add(file)
        val buf = IndexBuffer(file.buffer)
        indexBuffers.put(index, buf)
    }

    override fun getArchiveCompressed(archiveId: ArchiveId): CompressedFile? {
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
        return CompressedFile(dataBuffer.get(archive, idxEntry))
    }

    override fun getReferenceTableCompressed(index: Int): CompressedFile {
        return CompressedFile(dataBuffer.get(index, checkNotNull(referenceBuffer.get(index))))
    }

    override fun putReferenceTableCompressed(index: Int, compressedFile: CompressedFile) {
        putCompressed(ArchiveId(REFERENCE_INDEX, index), referenceBuffer, compressedFile)
    }

    override fun putArchiveCompressed(archiveId: ArchiveId, compressedFile: CompressedFile) {
        val idx = archiveId.index
        if (!indexBuffers.containsKey(idx)) {
            loadIndex(idx)
        }
        val idxBuffer = checkNotNull(indexBuffers[idx])
        putCompressed(archiveId, idxBuffer, compressedFile)
    }

    private fun putCompressed(archiveId: ArchiveId, indexBuffer: IndexBuffer, compressedFile: CompressedFile) {
        val data = compressedFile.buffer
        val length = compressedFile.compressedDataLength + CompressedFile.HEADER_LENGTH
        val sector = dataBuffer.sectorCount
        dataBuffer.append(archiveId, data)
        val indexEntry = IndexBuffer.Entry(length, sector)
        indexBuffer.put(archiveId.archive, indexEntry)
    }

    override fun close() {
        dataFile.close()
        referenceFile.close()
        indexFiles.forEach { it.close() }
    }
}