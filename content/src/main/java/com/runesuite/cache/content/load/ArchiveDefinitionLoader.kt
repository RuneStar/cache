package com.runesuite.cache.content.load

import com.runesuite.cache.content.def.CacheDefinition
import com.runesuite.cache.format.ReadableCache

abstract class ArchiveDefinitionLoader<out T : CacheDefinition>(
        val readableCache: ReadableCache,
        val index: Int
) : DefinitionLoader<T> {

    override fun getDefinition(index: Int): T? {
        val arch = readableCache.getArchive(this.index, index) ?: return null
        return newDefinition().apply { read(arch.records.first()) }
    }

    override fun getDefinitions(): List<T?> {
        return readableCache.getArchives(index).map { it?.let { newDefinition().apply { read(it.records.first()) } } }
    }

    override fun getNameHashes(): List<Int?> {
        return readableCache.getArchiveNameHashes(index)
    }

    override fun getDefinitionsCount(): Int {
        return readableCache.getArchiveCount(index)
    }

    abstract fun newDefinition(): T
}