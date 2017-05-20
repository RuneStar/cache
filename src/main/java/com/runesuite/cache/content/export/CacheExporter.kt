package com.runesuite.cache.content.export

import com.runesuite.cache.format.ReadableCache
import java.nio.file.Files
import java.nio.file.Path

abstract class CacheExporter(val cache: ReadableCache, val dir: Path) {

    init {
        if (!Files.isDirectory(dir)) {
            Files.createDirectory(dir)
        }
    }

    abstract fun export()

    private class Composite(cache: ReadableCache, dir: Path, private vararg val exporters: CacheExporter) : CacheExporter(cache, dir) {
        override fun export() {
            exporters.forEach { it.export() }
        }
    }

    companion object {
        fun all(cache: ReadableCache, dir: Path): CacheExporter {
            return Composite(cache, dir,
                    SpriteExporter(cache, dir.resolve("sprites"))
            )
        }
    }
}