package org.runestar.cache.test

import org.runestar.cache.content.def.ItemDefinition
import org.runestar.cache.format.BackedCache
import org.runestar.cache.format.fs.FileSystemCache
import org.runestar.cache.format.net.NetCache
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    BackedCache(
            FileSystemCache.open(),
            NetCache.open("oldschool1.runescape.com", 176)
    ).use { rc ->

        val sb = StringBuilder()

        ItemDefinition.Loader(rc).getDefinitions().forEachIndexed { index, record ->


            val def = record?.definition ?: return@forEachIndexed
            val name = stringToIdentifier(def.name) ?: return@forEachIndexed

            sb.append(index.toString()).append('\t')
            sb.append("${name}_${index}").append('\n')
        }

        Files.write(Paths.get("obj-names.tsv"), sb.toString().toByteArray())
    }
}


private val REMOVE_REGEX = "([']|<.*?>)".toRegex()

private val REPLACE_UNDERSCORE_REGEX = "[- /)(.,!]".toRegex()

private val REPLACE_DOLLARSIGN_REGEX = "[%&+?]".toRegex()

private val MULTI_UNDERSCORE_REGEX = "_{2,}".toRegex()

private val ENDS_UNDERSCORES_REGEX = "(^_+|_+$)".toRegex()

private fun stringToIdentifier(name: String): String? {
    if (name.equals("null", true)) return null
    if (name.isBlank()) return null
    var n = name.toLowerCase()
            .replace(REMOVE_REGEX, "")
            .replace(REPLACE_UNDERSCORE_REGEX, "_")
            .replace(REPLACE_DOLLARSIGN_REGEX, "_")
            .replace(ENDS_UNDERSCORES_REGEX, "")
            .replace(MULTI_UNDERSCORE_REGEX, "_")
    return n
}