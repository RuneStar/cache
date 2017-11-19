package com.runesuite.cache.content.load

import com.runesuite.cache.content.def.*

interface DefinitionLoader<out T : CacheDefinition> {

    fun getDefinition(index: Int): T?

    fun getDefinitions(): List<T?>

    fun getDefinitionsCount(): Int

    fun getNameHashes(): List<Int?>
}