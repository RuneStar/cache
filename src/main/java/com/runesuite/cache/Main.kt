package com.runesuite.cache

import com.runesuite.cache.format.BackedCache

fun main(args: Array<String>) {
    BackedCache.default().use { bc ->

//        bc.getArchive(Index.CONFIGS.id, Index.CONFIGS.ITEM)!!.files.forEachIndexed { i, f ->
//            ItemDefinition().apply {
//                read(f)
//                println("$i $this")
//            }
//        }
//
//        bc.getArchive(Index.CONFIGS.id, Index.CONFIGS.NPC)!!.files.forEachIndexed { i, f ->
//            NpcDefinition().apply {
//                read(f)
//                println("$i $this")
//            }
//        }
//
//        bc.getArchive(Index.CONFIGS.id, Index.CONFIGS.OBJECT)!!.files.forEachIndexed { i, f ->
//            ObjectDefinition().apply {
//                read(f)
//                println("$i $this")
//            }
//        }
    }
}