package com.runesuite.cache.content

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.runesuite.cache.content.def.ScriptDefinition
import com.runesuite.cache.content.load.*
import com.runesuite.cache.format.BackedStore
import com.runesuite.cache.format.ReadableCache
import com.runesuite.cache.format.fs.FileSystemStore
import com.runesuite.cache.format.net.NetStore

val mapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)



fun main(args: Array<String>) {
    ReadableCache(
            BackedStore(
                    FileSystemStore.open(),
                    NetStore.open()
            )
    ).use { rc ->

        ScriptDefinition.Loader(rc).getDefinitions().forEachIndexed { i, x -> println("$i $x") }
    }
}