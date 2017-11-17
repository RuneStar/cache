package com.runesuite.cache

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.collect.ArrayListMultimap
import com.runesuite.cache.format.BackedStore
import com.runesuite.cache.format.ReadableCache
import com.runesuite.cache.format.fs.FileSystemStore
import com.runesuite.cache.format.net.NetStore
import java.io.File
import java.util.*

val mapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

fun main(args: Array<String>) {
    ReadableCache(
            BackedStore(
                    FileSystemStore.open(),
                    NetStore.open()
            )
    ).use { bc ->

        val map = ArrayListMultimap.create<Int, Int>()
        for (idx in 0 until bc.getIndexCount()) {
            val names = bc.getArchiveNameHashes(idx)
            names.forEachIndexed { i, n ->
                if (n != null) {
                    map.put(idx, n)
                }
            }
        }
        mapper.writeValue(File("name-hashes.json"), map.asMap())
    }
}