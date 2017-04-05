package com.runesuite.cache.fs

import com.runesuite.cache.DataBuffer
import com.runesuite.cache.IndexBuffer
import java.io.Closeable
import java.nio.file.Path

class Store(val folder: Path) : AutoCloseable, Closeable {

    companion object {
        val MAIN_FILE_CACHE_DAT = "main_file_cache.dat2"
        val MAIN_FILE_CACHE_IDX = "main_file_cache.idx"
    }

    private val dataFile = BufFile(folder.resolve(MAIN_FILE_CACHE_DAT))
    val dataBuffer = DataBuffer(dataFile.buffer)

    private val referenceFile = BufFile(folder.resolve("${MAIN_FILE_CACHE_IDX}255"))
    val referenceBuffer = IndexBuffer(referenceFile.buffer)

    private val indexFiles: List<BufFile> = (0 until referenceBuffer.entryCount).map { BufFile(folder.resolve("$MAIN_FILE_CACHE_IDX$it")) }
    val indexBuffers = indexFiles.map { IndexBuffer(it.buffer) }

    override fun close() {
        dataFile.close()
        referenceFile.close()
        indexFiles.forEach { it.close() }
    }
}