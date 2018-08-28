package org.runestar.cache.content.load

import org.runestar.cache.content.def.CacheDefinition
import org.runestar.cache.format.ReadableCache
import java.util.concurrent.CompletableFuture

interface DefinitionLoader<out T : CacheDefinition> {

    fun getDefinition(id: Int): LoadedDefinition<T>?

    fun getDefinitions(): List<LoadedDefinition<T>?>

    fun getDefinitionsCount(): Int

    abstract class Archive<out T : CacheDefinition>(
            val readableCache: ReadableCache,
            val index: Int
    ) : DefinitionLoader<T> {

        override fun getDefinition(id: Int): LoadedDefinition.Archive<T>? {
            val archive = readableCache.getArchive(this.index, id).join() ?: return null
            val record = checkNotNull(archive.records.first())
            val def = newDefinition().apply { read(record.buffer) }
            return LoadedDefinition.Archive(archive, def)
        }

        override fun getDefinitions(): List<LoadedDefinition.Archive<T>?> {
            val archiveFutures = Array(getDefinitionsCount()) { readableCache.getArchive(index, it) }
            CompletableFuture.allOf(*archiveFutures).join()
            return archiveFutures.mapIndexed { i, af ->
                val archive = af.join() ?: return@mapIndexed null
                val record = checkNotNull(archive.records.first())
                val def = newDefinition().apply { read(record.buffer) }
                LoadedDefinition.Archive(archive, def)
            }
        }

        override fun getDefinitionsCount(): Int {
            return readableCache.getArchiveIds(index).last() + 1
        }

        abstract fun newDefinition(): T
    }

    abstract class Record<out T : CacheDefinition>(
            val readableCache: ReadableCache,
            val index: Int,
            val archive: Int
    ) : DefinitionLoader<T> {

        private val arch by lazy { checkNotNull(readableCache.getArchive(index, archive).join()) }

        override fun getDefinition(id: Int): LoadedDefinition.Record<T>? {
            val record = arch.records[id] ?: return null
            val def = newDefinition().apply { read(record.buffer) }
            return LoadedDefinition.Record(record, def)
        }

        override fun getDefinitions(): List<LoadedDefinition.Record<T>?> {
            return arch.records.mapIndexed { i, r ->
                if (r == null) return@mapIndexed null
                val def = newDefinition().apply { read(r.buffer) }
                LoadedDefinition.Record(r, def)
            }
        }

        override fun getDefinitionsCount(): Int {
            return arch.records.size
        }

        abstract fun newDefinition(): T
    }
}