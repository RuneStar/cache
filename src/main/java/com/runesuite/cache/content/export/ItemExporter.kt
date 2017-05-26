package com.runesuite.cache.content.export

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.runesuite.cache.content.Index
import com.runesuite.cache.content.def.ItemDefinition
import com.runesuite.cache.format.ReadableCache
import java.nio.file.Path

class ItemExporter(cache: ReadableCache, dir: Path) : CacheExporter(cache, dir) {

    private companion object {
        val mapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    }

    override fun export() {
        cache.getArchive(Index.CONFIGS.id, Index.CONFIGS.ITEM)?.files?.forEachIndexed { i, f ->
            val item = ItemDefinition()
            item.id = i
            item.read(f)
            mapper.writeValue(dir.resolve("$i.json").toFile(), item)
        }
    }
}