package com.runesuite.cache.format.fs

import com.runesuite.cache.extensions.closeQuietly
import com.runesuite.cache.format.Archive
import com.runesuite.cache.format.DefaultArchive
import com.runesuite.cache.format.IndexReference
import com.runesuite.cache.format.WritableCache
import mu.KotlinLogging
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class FileSystemCache
@Throws(IOException::class)
constructor(val folder: Path) : WritableCache {

    companion object {
        private val MAIN_FILE_CACHE_DAT = "main_file_cache.dat2"
        private val MAIN_FILE_CACHE_IDX = "main_file_cache.idx"
        private const val REFERENCE_INDEX = 255

        private const val MAX_INDEX_FILE_SIZE = 50_000
        private const val MAX_REFERENCE_FILE_SIZE = 1_000_000
        private const val MAX_DATA_FILE_SIZE = 200_000_000

        @Throws(IOException::class)
        fun default(): FileSystemCache {
            return FileSystemCache((Paths.get(System.getProperty("user.home"), "jagexcache", "oldschool", "LIVE")))
        }
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

    override fun getArchive(index: Int, archive: Int): Archive? {
        if (!indexBuffers.containsKey(index)) {
            loadIndex(index)
        }
        val idxBuffer = checkNotNull(indexBuffers[index])
        if (archive >= idxBuffer.entryCount) {
            return null
        }
        val idxEntry = idxBuffer.get(archive) ?: return null
        return DefaultArchive(dataBuffer.get(archive, idxEntry))
    }

    override fun getIndexReference(index: Int): IndexReference {
        val indexEntry = checkNotNull(referenceBuffer.get(index))
        return IndexReference(DefaultArchive(dataBuffer.get(index, indexEntry)))
    }

    override fun putIndexReference(index: Int, indexReference: IndexReference) {
        putArchive(REFERENCE_INDEX, index, referenceBuffer, indexReference.archive)
    }

    override fun putArchive(index: Int, archive: Int, data: Archive) {
        if (!indexBuffers.containsKey(index)) {
            loadIndex(index)
        }
        val idxBuffer = checkNotNull(indexBuffers[index])
        putArchive(index, archive, idxBuffer, data)
    }

    private fun putArchive(index: Int, archive: Int, indexBuffer: IndexBuffer, data: Archive) {
        val buffer = data.buffer
        val length = buffer.readableBytes()
        val sector = dataBuffer.sectorCount
        dataBuffer.append(index, archive, buffer)
        val indexEntry = IndexBuffer.Entry(length, sector)
        indexBuffer.put(archive, indexEntry)
    }

    override fun close() {
        indexFiles.forEach { it.closeQuietly() }
        referenceFile.closeQuietly()
        dataFile.close()
    }
}