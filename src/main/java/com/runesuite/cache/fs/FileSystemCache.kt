package com.runesuite.cache.fs

import com.runesuite.cache.ArchiveId
import com.runesuite.cache.CompressedFile
import com.runesuite.cache.ReadableCache
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class FileSystemCache(val folder: Path) : AutoCloseable, Closeable, ReadableCache {

    private companion object {
        val MAIN_FILE_CACHE_DAT = "main_file_cache.dat2"
        val MAIN_FILE_CACHE_IDX = "main_file_cache.idx"
        const val REFERENCE_INDEX = 255
        const val MAX_INDEX_FILE_SIZE = 20_000
        const val MAX_REFERENCE_FILE_SIZE = 1_000_000
        const val MAX_DATA_FILE_SIZE = 20_000_000
    }

    private val dataFile = BufFile(folder.resolve(MAIN_FILE_CACHE_DAT), MAX_DATA_FILE_SIZE)
    private val dataBuffer = DataBuffer(dataFile.buffer)

    private val referenceFile = BufFile(folder.resolve("$MAIN_FILE_CACHE_IDX$REFERENCE_INDEX"), MAX_REFERENCE_FILE_SIZE)
    private val referenceBuffer = IndexBuffer(referenceFile.buffer)

    private val indexFiles: MutableCollection<BufFile> = ArrayList()
    private val indexBuffers: TreeMap<Int, IndexBuffer> = TreeMap()

    init {
        Files.newDirectoryStream(folder).use {
            it.forEach {
                val name = it.fileName.toString()
                val idx = name.removePrefix(MAIN_FILE_CACHE_IDX).toIntOrNull()
                if (idx != null && idx != REFERENCE_INDEX) {
                    loadIndex(idx)
                }
            }
        }
    }

    override val indexCount: Int get() = indexBuffers.lastKey()?.let { it + 1 } ?: 0

    private fun loadIndex(index: Int) {
        val file = BufFile(folder.resolve("$MAIN_FILE_CACHE_IDX$index"), MAX_INDEX_FILE_SIZE)
        indexFiles.add(file)
        val buf = IndexBuffer(file.buffer)
        indexBuffers.put(index, buf)
    }

    override fun getArchiveCompressed(archiveId: ArchiveId): CompressedFile {
        val idx = archiveId.index
        require(idx <= indexCount)
        if (!indexBuffers.containsKey(idx)) {
           loadIndex(idx)
        }
        val indexBuffer = checkNotNull(indexBuffers[archiveId.index])
        val archive = archiveId.archive
        return CompressedFile(dataBuffer.get(archive, indexBuffer.get(archive)))
    }

    override fun getReferenceTableCompressed(index: Int): CompressedFile {
        return CompressedFile(dataBuffer.get(index, referenceBuffer.get(index)))
    }

    override fun close() {
        dataFile.close()
        referenceFile.close()
        indexFiles.forEach(BufFile::close)
    }
}