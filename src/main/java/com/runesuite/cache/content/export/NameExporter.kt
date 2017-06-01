package com.runesuite.cache.content.export

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.runesuite.cache.format.ReadableCache
import java.nio.file.Path

class NameExporter(cache: ReadableCache, dir: Path) : CacheExporter(cache, dir) {

    private companion object {
        val mapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    }

    override fun export() {
        val all = ArrayList<Collection<Int>>()
        for (i in 0 until cache.indices) {
            val ir = cache.getIndexReference(i)
            val hashes = ir.archives.filterNotNull().mapNotNull { it.nameHash }
            if (hashes.isNotEmpty()) {
                mapper.writeValue(dir.resolve("${i}_hashes.json").toFile(), hashes)
                all.add(hashes)
            }
        }
        mapper.writeValue(dir.resolve("all_hashes.json").toFile(), all.flatten())
    }
}