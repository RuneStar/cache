package com.runesuite.cache

import com.runesuite.cache.format.BackedCache

fun main(args: Array<String>) {
    BackedCache.Default().use { bc ->
        (0 until bc.indexCount).forEach {
            println(bc.getReferenceTable(it))
        }
    }
}