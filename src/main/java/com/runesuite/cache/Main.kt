package com.runesuite.cache

import com.runesuite.cache.extensions.readableArray

fun main(args: Array<String>) {
    BackedCache.Default().use {
        println((it.getArchiveCompressed(ArchiveId(8, 0))).data.readableArray().contentToString())
        println((it.getArchiveCompressed(ArchiveId(8, 1))).data.readableArray().contentToString())
        println((it.getArchiveCompressed(ArchiveId(8, 2))).data.readableArray().contentToString())
    }
}