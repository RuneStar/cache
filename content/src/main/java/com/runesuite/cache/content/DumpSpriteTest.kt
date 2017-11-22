package com.runesuite.cache.content

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.runesuite.cache.content.def.EnumDefinition
import com.runesuite.cache.content.def.SpriteSheetDefinition
import com.runesuite.cache.format.BackedStore
import com.runesuite.cache.format.ReadableCache
import com.runesuite.cache.format.fs.FileSystemStore
import com.runesuite.cache.format.net.NetStore
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

private val mapper = jacksonObjectMapper()

fun main(args: Array<String>) {


    ReadableCache(
            BackedStore(
                    FileSystemStore.open(),
                    NetStore.open()
            ),
            mapper.readValue(File("known-names.json"))
    ).use { rc ->

        val dir = Paths.get(".sprites")
        Files.createDirectories(dir)

        SpriteSheetDefinition.Loader(rc).getDefinitions().filterNotNull().forEach { ss ->
            val identifier = ss.archiveIdentifier
            val def = ss.getDefinition()

            val img = def.toImage()
            val fileName = identifier.name ?: identifier.nameHash!!.toString()
            val file = dir.resolve(fileName + ".png")
            ImageIO.write(img, "png", file.toFile())
        }

    }
}