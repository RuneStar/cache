package com.runesuite.cache.content

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

private val mapper = jacksonObjectMapper()

fun main(args: Array<String>) {
    val unknownNames = mapper.readValue<Set<Int>>(File("unknown-name-hashes.json"))

    val results = unhashStrings(
            setOf(
                    ",", " ", "_", "0", "1", "2", "3", "s",
                    "a"
            ),
            7,
            unknownNames
    )

    println()
    println()

    results.asMap().forEach { e ->
        println(e)
    }
}