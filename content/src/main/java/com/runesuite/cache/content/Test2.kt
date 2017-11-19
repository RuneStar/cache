package com.runesuite.cache.content

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

val mapper3 = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

fun main(args: Array<String>) {
    val strings = ArrayList<String>()
    for (x in 0..255) {
        for (y in 0..255) {
            strings.add("m${x}_$y")
            strings.add("l${x}_$y")
        }
        strings.add(emotes(x))
        strings.add(emotesLocked(x))
        strings.add(tabs(x))
        strings.add(orbXp(x))
        strings.add(reset(x))
        strings.add(optionsRadioButtons(x))
        strings.add(zeahBook(x))
        strings.add(magicon(x))
        strings.add(magicon2(x))
        strings.add(combaticons(x))
        strings.add(combaticons2(x))
        strings.add(combaticons3(x))
        strings.add(hitmark(x))
        strings.add(pengEmotes(x))
        strings.add(staticons(x))
        strings.add(staticons2(x))
        strings.add(barbassaultIcons(x))
        strings.add(orbIcon(x))
        strings.add(optionsIcons(x))
        strings.add(optionsSlider(x))
        strings.add(geIcons(x))
        strings.add(warningIcons(x))
        strings.add(closeButtons(x))
    }
    val names = mapper3.readValue<TreeMap<String, TreeMap<String, String>>>(File("names.json"))
    val stringsFile = mapper3.readValue<List<String>>(File("Strings.json"))
    strings.addAll(stringsFile)
    names.forEach { idx, ss ->
        ss.forEach { hash, n ->

            strings.forEach { string ->
                if (string.hashCode().toString() == hash) {
                    ss.put(hash, string)
                }
            }
        }
    }
    mapper3.writeValue(File("names2.json"), names)
}

fun emotes(id: Int) = "emotes,$id"
fun emotesLocked(id: Int) = "emotes_locked,$id"
fun tabs(id: Int) = "tabs,$id"
fun orbXp(id: Int) = "orb_xp,$id"
fun reset(id: Int) = "reset,$id"
fun optionsRadioButtons(id: Int) = "options_radio_buttons,$id"
fun zeahBook(id: Int) = "zeah_book,$id"
fun magicon(id: Int) = "magicon,$id"
fun magicon2(id: Int) = "magicon2,$id"
fun combaticons(id: Int) = "combaticons,$id"
fun combaticons2(id: Int) = "combaticons2,$id"
fun combaticons3(id: Int) = "combaticons3,$id"
fun hitmark(id: Int) = "hitmark,$id"
fun pengEmotes(id: Int) = "peng_emotes,$id"
fun staticons(id: Int) = "staticons,$id"
fun staticons2(id: Int) = "staticons2,$id"
fun barbassaultIcons(id: Int) = "barbassault_icons,$id"
fun orbIcon(id: Int) = "orb_icon,$id"
fun optionsIcons(id: Int) = "options_icons,$id"
fun optionsSlider(id: Int) = "options_slider,$id"
fun geIcons(id: Int) = "ge_icons,$id"
fun warningIcons(id: Int) = "warning_icons,$id"
fun closeButtons(id: Int) = "close_buttons,$id"