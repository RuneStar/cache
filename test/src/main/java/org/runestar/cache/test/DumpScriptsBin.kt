package org.runestar.cache.test

import org.runestar.cache.format.BackedCache
import org.runestar.cache.format.fs.FileSystemCache
import org.runestar.cache.format.net.NetCache
import org.runestar.cache.format.toArray
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    BackedCache(
            FileSystemCache.open(),
            NetCache.open("oldschool1.runescape.com", 176)
    ).use { rc ->

        val dir = Paths.get("input")
        Files.createDirectories(dir)

        println(rc.getArchiveIds(12).contentToString())
        for (a in rc.getArchiveIds(12).reversedArray()) {
            rc.getArchive(12, a).thenAccept { arch ->
                if (arch == null) {
                    println(a)
                    return@thenAccept
                }
                val buf = arch.records.first()!!.buffer
                val bs = buf.toArray(buf.readerIndex(), buf.readableBytes())
                Files.write(dir.resolve(a.toString()), bs)
            }
        }
    }
}