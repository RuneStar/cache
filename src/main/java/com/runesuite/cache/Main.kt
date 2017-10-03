package com.runesuite.cache

import com.runesuite.cache.format.BackedStore
import com.runesuite.cache.format.ReadableCache
import com.runesuite.cache.format.fs.FileSystemStore
import com.runesuite.cache.format.net.NetStore

fun main(args: Array<String>) {


    ReadableCache(BackedStore(FileSystemStore.open(), NetStore.open())).use { rc ->
        rc.getArchiveNameHashes(6).forEach {
            println(it)
        }
    }
}