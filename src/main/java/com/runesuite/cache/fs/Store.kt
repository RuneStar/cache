package com.runesuite.cache.fs

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
    }

    private val dataFile = BufFile(folder.resolve(MAIN_FILE_CACHE_DAT))
    val dataBuffer = DataBuffer(dataFile.buffer.slice())

    private val referenceFile = BufFile(folder.resolve("$MAIN_FILE_CACHE_IDX$REFERENCE_INDEX"))
    val referenceBuffer = IndexBuffer(referenceFile.buffer.slice())

    private val indexFiles: List<BufFile> = (0 until referenceBuffer.entryCount).map { BufFile(folder.resolve("$MAIN_FILE_CACHE_IDX$it")) }
    val indexBuffers = indexFiles.map { IndexBuffer(it.buffer.slice()) }

    fun get(index: Int, archive: Int): CompressedFile {
        val indexBuffer = if (index == REFERENCE_INDEX) {
            referenceBuffer
        } else {
            indexBuffers[index]
        }
        return CompressedFile.read(dataBuffer.get(archive, indexBuffer.get(archive)))
    }

    fun getReference(archive: Int): CompressedFile = get(REFERENCE_INDEX, archive)

    override fun close() {
        dataFile.close()
        referenceFile.close()
        indexFiles.forEach { it.close() }
    }
}