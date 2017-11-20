package com.runesuite.cache.content

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

private val mapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

fun main(args: Array<String>) {
    val unknownNames = mapper.readValue<Set<Int>>(File("unknown-name-hashes.json"))

    // seers_texture ?

    val results = unhashStrings(
            setOf(
                    ",", " ", "_", "0", "1", "2", "3", "s",
                    "tex", "stone", "rock", "floor", "wall", "grey", "gray", "white", "roof",
                    "shingle", "lava", "ice", "cobble", "wood", "oak", "stump", "tree", "water", "papyrus",
                    "ocean", "river", "frozen", "red", "light", "dark", "fire", "path", "road", "tzhaar",
                    "d", "ed", "chain", "mail", "black", "floor", "ground", "house", "hut", "top", "bottom", "city",
                    "small", "large", "big", "texture", "neon", "overlay", "magic", "way", "patch", "es", "falador",
                    "village", "shilo", "varrock", "park", "camelot", "seer", "seed", "farm", "ing", "wild", "wilderness",
                    "y", "evil", "mad", "left", "right", "top", "bottom","base"
            ),
            4,
            unknownNames
    )

    println()
    println()

    results.asMap().forEach { e ->
        println(e)
    }
}