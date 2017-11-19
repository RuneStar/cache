package com.runesuite.cache.content

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.runesuite.cache.format.BackedStore
import com.runesuite.cache.format.ReadableCache
import com.runesuite.cache.format.fs.FileSystemStore
import com.runesuite.cache.format.net.NetStore
import java.io.File

private val mapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

fun main(args: Array<String>) {

    val allNameHashes = ArrayList<Int>()

    ReadableCache(
            BackedStore(
                    FileSystemStore.open(),
                    NetStore.open()
            )
    ).use { rc ->
        for (idx in 0 until rc.getIndexCount()) {
            allNameHashes.addAll(rc.getArchiveIdentifiers(idx).mapNotNull { it?.nameHash })
        }
    }

    val strings = HashMap<Int, String>()
    fun String.addHash() {
        strings[this.hashCode()] = this
    }
    for (x in 0..255) {
        for (y in 0..255) {
            "m${x}_$y".addHash()
            "l${x}_$y".addHash()
        }
        "emotes,$x".addHash()
        "emotes_locked,$x".addHash()
        "tabs,$x".addHash()
        "orb_xp,$x".addHash()
        "reset,$x".addHash()
        "options_radio_buttons,$x".addHash()
        "zeah_book,$x".addHash()
        "magicon,$x".addHash()
        "magicon2,$x".addHash()
        "combaticons,$x".addHash()
        "combaticons2,$x".addHash()
        "combaticons3,$x".addHash()
        "hitmark,$x".addHash()
        "peng_emotes,$x".addHash()
        "staticons,$x".addHash()
        "staticons2,$x".addHash()
        "barbassault_icons,$x".addHash()
        "orb_icon,$x".addHash()
        "options_icons,$x".addHash()
        "options_slider,$x".addHash()
        "ge_icons,$x".addHash()
        "warning_icons,$x".addHash()
        "close_buttons,$x".addHash()
        "side_icons,$x".addHash()
        "steelborder,$x".addHash()
        "steelborder2,$x".addHash()
        "arrow,$x".addHash()
        "magicoff,$x".addHash()
        "magicoff2,$x".addHash()
        "miscgraphics,$x".addHash()
        "miscgraphics2,$x".addHash()
        "miscgraphics3,$x".addHash()
        "prayeroff,$x".addHash()
        "combatboxes,$x".addHash()
        "prayeron,$x".addHash()
        "mapfunction,$x".addHash()
        "sworddecor,$x".addHash()
        "wornicons,$x".addHash()
    }
    val singleStrings = mapper.readValue<List<String>>(File("single-strings.json"))
    singleStrings.forEach { it.addHash() }

    val dict = ArrayList<String>()
    val unknownNameHashes = ArrayList<Int>()

    allNameHashes.forEach { nameHash ->
        val v = strings[nameHash]
        if (v != null) {
            dict.add(v)
        } else {
            unknownNameHashes.add(nameHash)
        }
    }

    mapper.writeValue(File("all-name-hashes.json"), allNameHashes)
    mapper.writeValue(File("unknown-name-hashes.json"), unknownNameHashes)
    mapper.writeValue(File("known-names.json"), dict)
}