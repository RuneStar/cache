package com.runesuite.cache

import com.runesuite.cache.content.export.CacheExporter
import com.runesuite.cache.format.BackedCache
import java.nio.file.Paths

fun main(args: Array<String>) {
    BackedCache.default().use {
        CacheExporter.all(it, Paths.get(System.getProperty("user.home")).resolve("Desktop").resolve("cache")).export()
//        Thread.sleep(55_000)
//        for (i in 0 until it.indices) {
//            it.master.getIndexReference(i)
//        }
    }
}