package com.runesuite.cache.fs

import com.runesuite.cache.ArchiveId
import com.runesuite.cache.CompressedFile
import com.runesuite.cache.DataBuffer
import com.runesuite.cache.IndexBuffer
import java.io.Closeable
import java.nio.file.Path

class Store(val folder: Path) : AutoCloseable, Closeable {

    companion object {
        val MAIN_FILE_CACHE_DAT = "main_file_cache.dat2"
        val MAIN_FILE_CACHE_IDX = "main_file_cache.idx"
        const val REFERENCE_INDEX = 255
        const val MAX_INDEX_FILE_SIZE = 500_000
        const val MAX_DATA_FILE_SIZE = 10_000_000
    }

    private val dataFile = BufFile(folder.resolve(MAIN_FILE_CACHE_DAT), MAX_DATA_FILE_SIZE)
    val dataBuffer = DataBuffer(dataFile.buffer)

    private val referenceFile = BufFile(folder.resolve("$MAIN_FILE_CACHE_IDX$REFERENCE_INDEX"), MAX_INDEX_FILE_SIZE)
    val referenceBuffer = IndexBuffer(referenceFile.buffer)

    private val indexFiles: List<BufFile> = (0 until 16).map { BufFile(folder.resolve("$MAIN_FILE_CACHE_IDX$it"), MAX_INDEX_FILE_SIZE) }
    val indexBuffers = indexFiles.map { IndexBuffer(it.buffer) }

    fun getArchive(archiveId: ArchiveId): CompressedFile {
        val indexBuffer = indexBuffers[archiveId.index]
        val archive = archiveId.archive
        return CompressedFile(dataBuffer.get(archive, indexBuffer.get(archive)))
    }

    fun getReferenceTable(index: Int): CompressedFile {
        return CompressedFile(dataBuffer.get(index, referenceBuffer.get(index)))
    }
    override fun close() {
        dataFile.close()
        referenceFile.close()
        indexFiles.forEach { it.close() }
    }
}