package org.runestar.cache.test

import org.runestar.cache.content.def.EnumDefinition
import org.runestar.cache.format.BackedCache
import org.runestar.cache.format.fs.FileSystemCache
import org.runestar.cache.format.net.NetCache
import java.util.*

fun main(args: Array<String>) {
    BackedCache(
            FileSystemCache.open(),
            NetCache.open("oldschool1.runescape.com", 175)
    ).use { rc ->

        val set = TreeSet<Char>()
        EnumDefinition.Loader(rc).getDefinitions().forEachIndexed { i, r ->
            if (r == null) return@forEachIndexed
            val def = r.definition
            println(i)
            println("${def.defaultInt} ${def.defaultString}")
            println("@${def.keyType} @${def.valType}")
            println("${def.keys?.contentToString()}")
            println("${def.intVals?.contentToString()}")
            println("${def.stringVals?.contentToString()}")
            println()
            set.add(def.keyType)
            set.add(def.valType)
        }
        println(set)
    }
}