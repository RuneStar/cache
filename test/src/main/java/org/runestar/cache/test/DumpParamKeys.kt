package org.runestar.cache.test

import org.runestar.cache.content.def.ParamKey
import org.runestar.cache.content.def.VarbitDefinition
import org.runestar.cache.format.BackedCache
import org.runestar.cache.format.fs.FileSystemCache
import org.runestar.cache.format.net.NetCache
import java.util.*

fun main(args: Array<String>) {
    BackedCache(
            FileSystemCache.open(),
            NetCache.open("oldschool1.runescape.com", 176)
    ).use { rc ->

        val s = TreeSet<Int>()
        ParamKey.Loader(rc).getDefinitions().forEachIndexed { i, r ->
            if (r == null) return@forEachIndexed
            val def = r.definition
            if (def.type == 's') {
                s.add(i)
            }
            println("$i ${def.type}")
        }
        println(s)
    }
}