package com.runesuite.cache.content.load

import com.runesuite.cache.content.def.CacheDefinition
import com.runesuite.cache.format.ReadableCache

abstract class RecordDefinitionLoader<out T : CacheDefinition>(
        val readableCache: ReadableCache,
        val index: Int,
        val archive: Int
) : DefinitionLoader<T> {

    private val arch by lazy { readableCache.getArchive(index, archive)!! }

    override fun getDefinition(index: Int): T {
        return newDefinition().apply { read(arch.records[index]) }
    }

    override fun getDefinitions(): List<T> {
        return arch.records.map { newDefinition().apply { read(it) } }
    }

    override fun getNameHashes(): List<Int?> {
        return arrayOfNulls<Int>(getDefinitionsCount()).asList()
    }

    override fun getDefinitionsCount(): Int {
        return arch.size
    }

    abstract fun newDefinition(): T
}