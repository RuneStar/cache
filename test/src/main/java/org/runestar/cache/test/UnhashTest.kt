package org.runestar.cache.test

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
    // tempest
    // preservation
    // preserved
    // fossilized
    // lagoon

//    mapfunction

    // http://oldschoolrunescape.wikia.com/wiki/Unlisted_music_tracks
    // http://oldschoolrunescape.wikia.com/wiki/Massacre

    val results = unhashStrings(
            setOf(
                    "a", "b", "c"
            ),
            5,
            unknownNames
    )

    println()
    println()

    results.asMap().forEach { e ->
        println(e)
    }
}