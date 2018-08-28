package org.runestar.cache.content.load

import org.runestar.cache.content.def.CacheDefinition

interface LoadedDefinition<out T : CacheDefinition> {

    val nameHash: Int?

    val definition: T

    data class Archive<out T : CacheDefinition>(
            val archive: org.runestar.cache.format.Archive,
            override val definition: T
    ) : LoadedDefinition<T> {

        override val nameHash: Int? get() = archive.nameHash
    }

    data class Record<out T : CacheDefinition>(
            val record: org.runestar.cache.format.Record,
            override val definition: T
    ) : LoadedDefinition<T> {

        override val nameHash: Int? get() = record.nameHash
    }
}