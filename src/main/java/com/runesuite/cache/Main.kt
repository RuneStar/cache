package com.runesuite.cache

import com.runesuite.cache.fs.Store
import java.nio.file.Paths

fun main(args: Array<String>) {
//    CacheClient(139, "oldschool7.runescape.com", 43594).use {
//        println(it.request(FileId(255, 0)).get().compressedFile)
//    }
    Store(Paths.get(System.getProperty("user.home"), "jagexcache", "oldschool", "LIVE")).use {
        println(it.get(255, 0))
        println(it.referenceBuffer.getAll())
    }
}