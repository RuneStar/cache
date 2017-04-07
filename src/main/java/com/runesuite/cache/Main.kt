package com.runesuite.cache

import com.runesuite.cache.extensions.readableToString
import com.runesuite.cache.fs.Store
import com.runesuite.cache.net.CacheClient
import com.runesuite.cache.net.FileId
import java.nio.file.Paths

fun main(args: Array<String>) {
    CacheClient(139, "oldschool7.runescape.com", 43594).use {
        println(it.request(FileId(3, 1)).get().compressedFile.decompress().readableToString())
    }
    Store(Paths.get(System.getProperty("user.home"), "jagexcache", "oldschool", "LIVE")).use {
        println(it.get(3, 1).decompress().readableToString())
    }
}