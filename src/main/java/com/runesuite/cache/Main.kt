package com.runesuite.cache

import com.runesuite.cache.fs.FileSystemCache
import com.runesuite.cache.net.NetClientCache
import java.nio.file.Paths

fun main(args: Array<String>) {
    NetClientCache(139, "oldschool7.runescape.com", 43594).use {
//        println(it.createChecksumTable())
        println(it.getChecksumTable())
//        println(it.getReferenceTable(0))
//        println(it.getArchiveCompressed(ArchiveId(0, 0)))
    }
    FileSystemCache(Paths.get(System.getProperty("user.home"), "jagexcache", "oldschool", "LIVE")).use {
        println(it.getChecksumTable())
//        println(it.getReferenceTable(0))
//        println(it.getArchiveCompressed(ArchiveId(0, 0)))
    }
}