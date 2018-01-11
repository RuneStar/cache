package org.runestar.cache.format.fs

import org.runestar.cache.format.*
import org.kxtra.lang.autocloseable.closeQuietly
import java.io.IOException
import java.nio.channels.Channel
import java.nio.channels.ClosedChannelException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CompletableFuture

class FileSystemStore
@Throws(IOException::class)
private constructor(
        val directory: Path
) : WritableStore, Channel {

    private val dataFile: BufFile
    private val dataBuffer: DataBuffer

    private val referenceFile: BufFile
    private val referenceBuffer: IndexBuffer

    private val indexFiles: MutableCollection<BufFile> = ArrayList()
    private val indexBuffers: TreeMap<Int, IndexBuffer> = TreeMap()

    private var open = true

    init {
        dataFile = BufFile(directory.resolve(MAIN_FILE_CACHE_DAT), MAX_DATA_FILE_SIZE)
        try {
            referenceFile = BufFile(directory.resolve("$MAIN_FILE_CACHE_IDX$REFERENCE_INDEX"), MAX_REFERENCE_FILE_SIZE)
        } catch (newReferenceFileException: IOException) {
            dataFile.closeQuietly()
            throw newReferenceFileException
        }
        dataBuffer = DataBuffer(dataFile.buffer)
        referenceBuffer = IndexBuffer(referenceFile.buffer)
    }

    @Throws(ClosedChannelException::class)
    private fun checkOpen() {
        if (!isOpen) throw ClosedChannelException()
    }

    @Throws(IOException::class)
    private fun ensureIndexLoaded(index: Int) {
        if (indexBuffers.containsKey(index)) return
        val file: BufFile
        try {
            file = BufFile(directory.resolve("$MAIN_FILE_CACHE_IDX$index"), MAX_INDEX_FILE_SIZE)
        } catch (newFileException: IOException) {
            closeQuietly(newFileException)
            throw newFileException
        }
        indexFiles.add(file)
        indexBuffers[index] = IndexBuffer(file.buffer)
    }

    override fun getIndexReference(index: Int): CompletableFuture<IndexReference> {
        checkOpen()
        val indexEntry = checkNotNull(referenceBuffer.get(index))
        val indexRef = IndexReference(CompressedVolume(dataBuffer.get(index, indexEntry)))
        return CompletableFuture.completedFuture(indexRef)
    }

    override fun getReference(): CompletableFuture<StoreReference> {
        checkOpen()
        val indexCount = referenceBuffer.entryCount
        val refEntries = (0 until indexCount).map {
            val indexRef = getIndexReference(it).join()
            StoreReference.IndexReferenceInfo(indexRef.volume.crc, indexRef.version)
        }
        return CompletableFuture.completedFuture(StoreReference(refEntries))
    }

    override fun getVolume(index: Int, volume: Int): CompletableFuture<Volume?> {
        checkOpen()
        ensureIndexLoaded(index)
        val idxBuffer = checkNotNull(indexBuffers[index])
        if (volume >= idxBuffer.entryCount) return CompletableFuture.completedFuture(null)
        val idxEntry = idxBuffer.get(volume) ?: return CompletableFuture.completedFuture(null)
        return CompletableFuture.completedFuture(CompressedVolume(dataBuffer.get(volume, idxEntry)))
    }

    override fun setIndexReference(index: Int, value: IndexReference) {
        checkOpen()
        setVolumeIdx(REFERENCE_INDEX, index, referenceBuffer, value.volume)
    }

    override fun setReference(value: StoreReference) {
        checkOpen()
        require(getReference().join() == value) { "cannot directly set a StoreReference" }
    }

    override fun setVolume(index: Int, volume: Int, value: Volume) {
        checkOpen()
        ensureIndexLoaded(index)
        val idxBuffer = checkNotNull(indexBuffers[index])
        setVolumeIdx(index, volume, idxBuffer, value)
    }

    private fun setVolumeIdx(index: Int, volume: Int, indexBuffer: IndexBuffer, value: Volume) {
        checkOpen()
        val buffer = CompressedVolume.fromVolume(value).buffer
        val length = buffer.readableBytes()
        val sector = dataBuffer.sectorCount
        dataBuffer.append(index, volume, buffer)
        val indexEntry = IndexBuffer.Entry(length, sector)
        indexBuffer.set(volume, indexEntry)
    }

    override fun isOpen() = open

    @Throws(IOException::class)
    override fun close() {
        if (open) {
            open = false
            indexFiles.forEach {
                it.closeQuietly()
            }
            dataFile.use {
                referenceFile.close()
            }
        }
    }

    companion object {
        private val MAIN_FILE_CACHE_DAT = "main_file_cache.dat2"
        private val MAIN_FILE_CACHE_IDX = "main_file_cache.idx"
        private const val REFERENCE_INDEX = 255

        private const val MAX_INDEX_FILE_SIZE = 50_000
        private const val MAX_REFERENCE_FILE_SIZE = 1_000_000
        private const val MAX_DATA_FILE_SIZE = 150_000_000

        @JvmField
        val DEFAULT_DIRECTORY: Path =
                Paths.get(System.getProperty("user.home"), "jagexcache", "oldschool", "LIVE")

        @JvmStatic
        @Throws(IOException::class)
        fun open(
                directory: Path = DEFAULT_DIRECTORY
        ): FileSystemStore {
            return FileSystemStore(directory)
        }
    }
}