package com.runesuite.cache.format.fs

import com.runesuite.cache.format.CacheReference
import com.runesuite.cache.format.IndexReference
import com.runesuite.cache.format.Volume
import com.runesuite.cache.format.WritableStore
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture

class FileSystemStore(
        val directory: Path = Paths.get(System.getProperty("user.home"), "jagexcache", "oldschool", "LIVE")
) : WritableStore {

    override fun getIndexReference(index: Int): CompletableFuture<IndexReference> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getReference(): CompletableFuture<CacheReference> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getVolume(index: Int, archive: Int): CompletableFuture<Volume> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setIndexReference(index: Int, indexReference: IndexReference) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setReference(reference: CacheReference) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setVolume(index: Int, archive: Int, volume: Volume) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}