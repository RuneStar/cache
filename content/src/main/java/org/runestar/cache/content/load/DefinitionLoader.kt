package org.runestar.cache.content.load

import org.runestar.cache.content.def.*
import org.runestar.cache.format.ArchiveIdentifier
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
            val def = newDefinition().apply { read(archive.records.first()) }
            return LoadedDefinition.Archive(
                    ArchiveIdentifier(id, archive.identifier.nameHash, archive.identifier.name),
                    def
            )
        }

        override fun getDefinitions(): List<LoadedDefinition.Archive<T>?> {
            val archiveFutures = Array(readableCache.getArchiveCount(index)) { readableCache.getArchive(index, it) }
            CompletableFuture.allOf(*archiveFutures).join()
            return archiveFutures.mapIndexed { i, af ->
                val archive = af.join() ?: return@mapIndexed null
                val def = newDefinition().apply { read(archive.records.first()) }
                LoadedDefinition.Archive(
                        ArchiveIdentifier(i, archive.identifier.nameHash, archive.identifier.name),
                        def
                )
            }
        }

        override fun getDefinitionsCount(): Int {
            return readableCache.getArchiveCount(index)
        }

        abstract fun newDefinition(): T
    }

    abstract class Record<out T : CacheDefinition>(
            val readableCache: ReadableCache,
            val index: Int,
            val archive: Int
    ) : DefinitionLoader<T> {

        private val arch by lazy { checkNotNull(readableCache.getArchive(index, archive).join()) }

        override fun getDefinition(id: Int): LoadedDefinition.Record<T> {
            val def = newDefinition().apply { read(arch.records[id]) }
            return LoadedDefinition.Record(id, def)
        }

        override fun getDefinitions(): List<LoadedDefinition.Record<T>> {
            return arch.records.mapIndexed { i, bb ->
                val def = newDefinition().apply { read(bb) }
                LoadedDefinition.Record(i, def)
            }
        }

        override fun getDefinitionsCount(): Int {
            return arch.records.size
        }

        abstract fun newDefinition(): T
    }
}