package com.runesuite.cache.format.fs

import com.runesuite.cache.extensions.closeQuietly
import com.runesuite.cache.format.Container
import com.runesuite.cache.format.DefaultContainer
import com.runesuite.cache.format.IndexReference
import com.runesuite.cache.format.WritableCache
import mu.KotlinLogging
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class FileSystemCache
@Throws(IOException::class)
constructor(val folder: Path) : WritableCache() {

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

    private var isOpen = true

    override fun isOpen() = isOpen

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

    override fun getContainer(index: Int, archive: Int): Container? {
        check(isOpen)
        if (!indexBuffers.containsKey(index)) {
            loadIndex(index)
        }
        val idxBuffer = checkNotNull(indexBuffers[index])
        if (archive >= idxBuffer.entryCount) {
            return null
        }
        val idxEntry = idxBuffer.get(archive) ?: return null
        return DefaultContainer(dataBuffer.get(archive, idxEntry))
    }

    override fun getIndexReference(index: Int): IndexReference {
        check(isOpen)
        val indexEntry = checkNotNull(referenceBuffer.get(index))
        return IndexReference(DefaultContainer(dataBuffer.get(index, indexEntry)))
    }

    override fun setIndexReference(index: Int, indexReference: IndexReference) {
        setContainerIdx(REFERENCE_INDEX, index, referenceBuffer, indexReference.container)
    }

    override fun setContainer(index: Int, archive: Int, data: Container) {
        if (!indexBuffers.containsKey(index)) {
            loadIndex(index)
        }
        val idxBuffer = checkNotNull(indexBuffers[index])
        setContainerIdx(index, archive, idxBuffer, data)
    }

    private fun setContainerIdx(index: Int, archive: Int, indexBuffer: IndexBuffer, data: Container) {
        check(isOpen)
        val buffer = data.buffer
        val length = buffer.readableBytes()
        val sector = dataBuffer.sectorCount
        dataBuffer.append(index, archive, buffer)
        val indexEntry = IndexBuffer.Entry(length, sector)
        indexBuffer.set(archive, indexEntry)
    }

    override fun close() {
        if (isOpen) {
            isOpen = false
            indexFiles.forEach { it.closeQuietly() }
            referenceFile.closeQuietly()
            dataFile.close()
        }
    }
}