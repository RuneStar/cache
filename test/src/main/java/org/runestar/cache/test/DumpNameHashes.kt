package org.runestar.cache.test

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
    ).use { bc ->
        val s = StringBuilder()
        for (i in 0 until bc.getIndexCount()) {
            val ref = bc.getIndexReference(i).join()
            if (!ref.hasNames) continue
            for (a in ref.archiveIds) {
                val archive = checkNotNull(ref.archives[a])
                val hash = checkNotNull(archive.nameHash)
                s.appendln("$i,$a,-1,$hash")
                for (r in archive.recordIds) {
                    val record = checkNotNull(archive.records[r])
                    val hash2 = checkNotNull(record.nameHash)
                    if (hash2 != 0) {
                        s.appendln("$i,$a,$r,$hash2")
                    }
                }
            }
        }

        Files.write(Paths.get("names.csv"), s.toString().toByteArray(), StandardOpenOption.CREATE)
    }
}