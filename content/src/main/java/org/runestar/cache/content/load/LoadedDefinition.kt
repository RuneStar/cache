package org.runestar.cache.content.load

import org.runestar.cache.content.def.CacheDefinition
import org.runestar.cache.format.ArchiveIdentifier

interface LoadedDefinition<out T : CacheDefinition> {

    fun getId(): Int

    fun getDefinition(): T

    data class Archive<out T : CacheDefinition>(
            val archiveIdentifier: ArchiveIdentifier,
            private val definition: T
    ) : LoadedDefinition<T> {

        override fun getDefinition(): T {
            return definition
        }

        override fun getId(): Int {
            return archiveIdentifier.id
        }
    }

    data class Record<out T : CacheDefinition>(
            private val id: Int,
            private val definition: T
    ) : LoadedDefinition<T> {

        override fun getDefinition(): T {
            return definition
        }

        override fun getId(): Int {
            return id
        }
    }
}