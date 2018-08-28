package org.runestar.cache.test

import org.runestar.cache.content.def.SpriteSheetDefinition
import org.runestar.cache.format.BackedCache
import org.runestar.cache.format.fs.FileSystemCache
import org.runestar.cache.format.net.NetCache
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

fun main(args: Array<String>) {

    BackedCache(
            FileSystemCache.open(),
            NetCache.open("oldschool1.runescape.com", 174)
    ).use { rc ->

        val dir = Paths.get(".sprites")
        Files.createDirectories(dir)

        SpriteSheetDefinition.Loader(rc).getDefinitions().filterNotNull().forEach { ss ->

            val def = ss.definition

            val img = def.toImage()
            val fileName = ss.nameHash.toString()
            val file = dir.resolve(fileName + ".png")
            ImageIO.write(img, "png", file.toFile())
        }

    }
}