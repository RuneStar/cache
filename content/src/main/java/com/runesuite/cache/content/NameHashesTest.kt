package com.runesuite.cache.content

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.runesuite.cache.format.BackedStore
import com.runesuite.cache.format.ReadableCache
import com.runesuite.cache.format.fs.FileSystemStore
import com.runesuite.cache.format.net.NetStore
import com.runesuite.general.updateRevision
import java.io.File

private val mapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
private val writer = mapper.writer(
        DefaultPrettyPrinter().apply {
            indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
        }
)

fun main(args: Array<String>) {

    updateRevision()

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
        "clickcross,$x".addHash()
        "worldmap_icon,$x".addHash()
        "bankbuttons,$x".addHash()
    }
    val individualNames = mapper.readValue<List<String>>(File("individual-names.json"))
    individualNames.forEach { it.addHash() }

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

    writer.writeValue(File("all-name-hashes.json"), allNameHashes.toSortedSet())
    writer.writeValue(File("unknown-name-hashes.json"), unknownNameHashes.toSortedSet())
    writer.writeValue(File("known-names.json"), dict.toSortedSet())
}