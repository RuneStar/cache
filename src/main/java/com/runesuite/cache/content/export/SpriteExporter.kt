package com.runesuite.cache.content.export

import com.runesuite.cache.content.Index
import com.runesuite.cache.content.StringHashes
import com.runesuite.cache.content.def.SpriteSheetDefinition
import com.runesuite.cache.format.ReadableCache
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO

class SpriteExporter(cache: ReadableCache, dir: Path) : CacheExporter(cache, dir) {

    override fun export() {
        cache.getIndexReference(Index.SPRITES.id).archives.forEach { ai ->
            val a = checkNotNull(ai)
            val name = StringHashes.known[a.nameHash] ?: a.nameHash.toString()
            cache.getArchive(Index.SPRITES.id, a.id)?.let { r ->
                check(r.files.size == 1)
                val ss = SpriteSheetDefinition()
                ss.id = a.id
                ss.read(r.files[0])
                val img = ss.combine()
                ImageIO.write(img, "png", dir.resolve(name + ".png").toFile())
            }
        }
    }

    fun SpriteSheetDefinition.combine(): BufferedImage {
        val bi = BufferedImage(spriteWidth * sprites.size, spriteHeight, SpriteSheetDefinition.IMAGE_TYPE)
        val g = bi.graphics
        sprites.forEachIndexed { i, s ->
            g.drawImage(s.image, i * spriteWidth, 0, null)
        }
        g.dispose()
        return bi
    }
}