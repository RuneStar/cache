package com.runesuite.cache

import com.runesuite.cache.extensions.readableToString
import com.runesuite.cache.fs.Store
import com.runesuite.cache.net.CacheClient
import java.nio.file.Paths

fun main(args: Array<String>) {
    CacheClient(139, "oldschool7.runescape.com", 43594).use {
        println(it.request(255, 255).get().data.readableToString())
        println(it.request(255, 0).get().data.readableToString())
    }
    Store(Paths.get(System.getProperty("user.home"), "jagexcache", "oldschool", "LIVE")).use {
        println(it.referenceBuffer.readAll()) 
    }
}