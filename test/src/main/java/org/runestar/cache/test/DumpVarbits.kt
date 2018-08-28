package org.runestar.cache.test

import org.runestar.cache.content.def.VarbitDefinition
import org.runestar.cache.format.BackedCache
import org.runestar.cache.format.fs.FileSystemCache
import org.runestar.cache.format.net.NetCache
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

fun main(args: Array<String>) {
    BackedCache(
            FileSystemCache.open(),
            NetCache.open("oldschool1.runescape.com", 174)
    ).use { rc ->

        val s = StringBuilder()
        VarbitDefinition.Loader(rc).getDefinitions().forEachIndexed { i, r ->
            if (r == null) return@forEachIndexed
            val def = r.definition
            s.append(i)
            s.append(',')
            s.append(def.index)
            s.append(',')
            s.append(def.first)
            s.append(',')
            s.appendln(def.last)
        }

        Files.write(Paths.get("varbits.csv"), s.toString().toByteArray(), StandardOpenOption.CREATE_NEW)
    }
}