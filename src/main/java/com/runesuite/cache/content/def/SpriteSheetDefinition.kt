package com.runesuite.cache.content.def

import com.runesuite.cache.extensions.setEach
import com.runesuite.cache.extensions.toUnsigned
import io.netty.buffer.ByteBuf
import java.awt.image.BufferedImage

class SpriteSheetDefinition : CacheDefinition() {

    companion object {
        const val IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB
    }

    class Sprite {
        var offsetX: Int = 0
        var offsetY: Int = 0
        var subWidth: Int = 0
        var subHeight: Int = 0
        lateinit var image: BufferedImage
    }

    var spriteWidth: Int = 0
    var spriteHeight: Int = 0
    lateinit var sprites: Array<Sprite>

    override fun read(buffer: ByteBuf) {
        val spriteCount = buffer.getUnsignedShort(buffer.writerIndex() - 2)
        check(spriteCount > 0)
        buffer.markReaderIndex()
        buffer.readerIndex(buffer.writerIndex() - 7 - spriteCount * 8)
        spriteWidth = buffer.readUnsignedShort()
        spriteHeight = buffer.readUnsignedShort()
        val paletteLength = buffer.readUnsignedByte() + 1
        sprites = Array(spriteCount) {
            Sprite().apply { image = BufferedImage(spriteWidth, spriteHeight, IMAGE_TYPE) }
        }
        sprites.forEach { it.offsetX = buffer.readUnsignedShort() }
        sprites.forEach { it.offsetY = buffer.readUnsignedShort() }
        sprites.forEach { it.subWidth = buffer.readUnsignedShort() }
        sprites.forEach { it.subHeight = buffer.readUnsignedShort() }
        buffer.readerIndex(buffer.writerIndex() - 7 - spriteCount * 8 - (paletteLength - 1) * 3)
        val palette = IntArray(paletteLength) {
            if (it == 0) return@IntArray 0
            buffer.readUnsignedMedium().takeUnless { it == 0 } ?: 1
        }
        buffer.resetReaderIndex()
        sprites.forEach { s ->
            val dimension = s.subWidth * s.subHeight
            val pixelPaletteIndices = ByteArray(dimension)
            val pixelAlphas = ByteArray(dimension)
            val flags = buffer.readUnsignedByte().toInt()
            val isVertical = flags and Flag.VERTICAL.id != 0
            val isAlpha = flags and Flag.ALPHA.id != 0
            iterateRectangle1d(s.subWidth, s.subHeight, isVertical) {
                pixelPaletteIndices[it] = buffer.readByte()
            }
            if (isAlpha) {
                iterateRectangle1d(s.subWidth, s.subHeight, isVertical) {
                    pixelAlphas[it] = buffer.readByte()
                }
            } else {
                pixelAlphas.setEach {
                    if (pixelPaletteIndices[it].toInt() != 0) 0xFF.toByte() else 0
                }
            }
            for (x in 0 until s.subWidth) {
                for (y in 0 until s.subHeight) {
                    val pixelPos = s.subWidth * y + x
                    val paletteIndex = pixelPaletteIndices[pixelPos].toUnsigned()
                    val pixelValue = palette[paletteIndex] or (pixelAlphas[pixelPos].toUnsigned() shl 24)
                    s.image.setRGB(x + s.offsetX, y + s.offsetY, pixelValue)
                }
            }
        }
    }

    private inline fun iterateRectangle1d(width: Int, height: Int, vertical: Boolean, action: (Int) -> Unit) {
        if (vertical) {
            for (i in 0 until width) {
                for (j in 0 until height) {
                    action.invoke(j * width + i)
                }
            }
        } else {
            for (i in 0 until height) {
                for (j in 0 until width) {
                    action.invoke(i * width + j)
                }
            }
        }
    }

    private enum class Flag(idPosition: Int) {
        VERTICAL(0),
        ALPHA(1);

        val id = 1 shl idPosition
    }
}