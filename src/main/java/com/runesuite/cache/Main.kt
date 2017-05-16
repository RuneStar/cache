package com.runesuite.cache

import com.runesuite.cache.content.def.ItemDefinition
import com.runesuite.cache.content.def.NpcDefinition
import com.runesuite.cache.content.def.ObjectDefinition
import com.runesuite.cache.format.BackedCache

fun main(args: Array<String>) {
    BackedCache.default().use { bc ->

        bc.getArchive(2, 10)!!.files.forEachIndexed { i, f ->
            ItemDefinition().apply {
                read(f)
                println("$i $this")
            }
        }

        bc.getArchive(2, 9)!!.files.forEachIndexed { i, f ->
            NpcDefinition().apply {
                read(f)
                println("$i $this")
            }
        }

        bc.getArchive(2, 6)!!.files.forEachIndexed { i, f ->
            ObjectDefinition().apply {
                read(f)
                println("$i $this")
            }
        }

//        for (i in 0 until bc.indices) {
//            bc.getIndexReference(i).archives.forEach { a ->
//                a?.nameHash?.takeIf { it != 0 }?.let { println("$i ${a.id} $it") }
//            }
//        }
    }
}