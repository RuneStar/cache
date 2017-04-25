package com.runesuite.cache

import com.runesuite.cache.format.BackedCache

fun main(args: Array<String>) {
    BackedCache.Default().use { bc ->
        (0 until bc.indices).forEach {

        }
//        println(bc.getIndexReferences(13).archives)
//        println(bc.getArchive(ArchiveId(13, 819)))
    }
}