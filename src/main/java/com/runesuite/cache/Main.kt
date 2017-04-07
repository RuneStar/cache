package com.runesuite.cache

import com.runesuite.cache.extensions.readableToString
import com.runesuite.cache.fs.Store
import com.runesuite.cache.net.CacheClient
import com.runesuite.cache.net.FileId
import java.nio.file.Paths

fun main(args: Array<String>) {
    CacheClient(139, "oldschool7.runescape.com", 43594).use {
        it.request(FileId(255, 255)).get()
        it.request(FileId(255, 0)).get()
        it.request(FileId(255, 1)).get()
        it.request(FileId(255, 2)).get()
    }
    Store(Paths.get(System.getProperty("user.home"), "jagexcache", "oldschool", "LIVE")).use {
        println(it.indexBuffers[3].getAll())
        println(it.get(3, 0).readableToString())
    }
}