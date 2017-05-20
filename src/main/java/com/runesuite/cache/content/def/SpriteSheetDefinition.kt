package com.runesuite.cache.content.def

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
        sprites.forEach {
            it.offsetX = buffer.readUnsignedShort()
        }
        sprites.forEach {
            it.offsetY = buffer.readUnsignedShort()
        }
        sprites.forEach {
            it.subWidth = buffer.readUnsignedShort()
        }
        sprites.forEach {
            it.subHeight = buffer.readUnsignedShort()
        }
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
            if (!isVertical) {
                for (d in 0 until dimension) {
                    pixelPaletteIndices[d] = buffer.readByte()
                }
            } else {
                for (w in 0 until s.subWidth) {
                    for (h in 0 until s.subHeight) {
                        pixelPaletteIndices[s.subWidth * h + w] = buffer.readByte()
                    }
                }
            }
            if (isAlpha) {
                if (!isVertical) {
                    for (d in 0 until dimension) {
                        pixelAlphas[d] = buffer.readByte()
                    }
                } else {
                    for (w in 0 until s.subWidth) {
                        for (h in 0 until s.subHeight) {
                            pixelAlphas[s.subWidth * h + w] = buffer.readByte()
                        }
                    }
                }
            } else {
                pixelPaletteIndices.forEachIndexed { i, ppi ->
                    if (ppi.toInt() != 0) {
                        pixelAlphas[i] = 0xFF.toByte()
                    }
                }
            }
            for (x in 0 until s.subWidth) {
                for (y in 0 until s.subHeight) {
                    val idx = s.subWidth * y + x
                    val index = pixelPaletteIndices[idx].toUnsigned()
                    val px = palette[index] or (pixelAlphas[idx].toUnsigned() shl 24)
                    s.image.setRGB(x + s.offsetX, y + s.offsetY, px)
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