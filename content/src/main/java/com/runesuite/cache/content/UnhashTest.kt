package com.runesuite.cache.content

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

private val mapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

fun main(args: Array<String>) {
    val unknownNames = mapper.readValue<Set<Int>>(File("unknown-name-hashes.json"))

    // seers_texture ?

    // #
    // the doors of dinh
    // fire in the deep
    // ice and fire
    // night of the vampyre
    // showdown
    // tempest
    // preservation
    // preserved
    // fossilized
    // lagoon

    // http://oldschoolrunescape.wikia.com/wiki/Unlisted_music_tracks
    // http://oldschoolrunescape.wikia.com/wiki/Massacre

    val results = unhashStrings(
            setOf(
                    "_", ",", "0", "1", "2", "3", "s", "icon", "-", " ", "'", "4",
                    "a"
            ),
            6,
            unknownNames
    )

    println()
    println()

    results.asMap().forEach { e ->
        println(e)
    }
}