package com.runesuite.cache

import com.runesuite.cache.fs.Cache
import com.runesuite.cache.fs.Store
import com.runesuite.cache.net.CacheClient
import com.runesuite.cache.net.FileId
import java.nio.file.Paths

fun main(args: Array<String>) {
    CacheClient(139, "oldschool7.runescape.com", 43594).use {
        println(ChecksumTable.read(it.request(FileId(255, 255)).get().compressedFile.decompress()))
    }
    Cache(Store(Paths.get(System.getProperty("user.home"), "jagexcache", "oldschool", "LIVE"))).use {
        println(it.checksumTable)
    }
}