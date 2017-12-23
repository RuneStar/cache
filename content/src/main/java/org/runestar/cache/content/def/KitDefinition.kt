package org.runestar.cache.content.def

import org.runestar.cache.content.load.DefinitionLoader
import org.runestar.cache.format.ReadableCache
import io.netty.buffer.ByteBuf
import java.util.*

class KitDefinition : CacheDefinition() {

    var colorFind: ShortArray? = null
    var colorReplace: ShortArray? = null
    var textureFind: ShortArray? = null
    var textureReplace: ShortArray? = null
    var bodyPartId = -1;
    var modelIds: IntArray? = null
    var models: IntArray = intArrayOf(-1, -1, -1, -1, -1)
    var nonSelectable = false

    override fun read(buffer: ByteBuf) {
        while (true) {
            val opcode = buffer.readUnsignedByte().toInt()
            when (opcode) {
                0 -> return
                1 -> bodyPartId = buffer.readUnsignedByte().toInt()
                2 -> {
                    val length = buffer.readUnsignedByte().toInt()
                    modelIds = IntArray(length) { buffer.readUnsignedShort() }
                }
                3 -> nonSelectable = true
                40 -> {
                    val colors = buffer.readUnsignedByte().toInt()
                    colorFind = ShortArray(colors)
                    colorReplace = ShortArray(colors)
                    for (i in 0 until colors) {
                        colorFind!![i] = buffer.readShort()
                        colorReplace!![i] = buffer.readShort()
                    }
                }
                41 -> {
                    val textures = buffer.readUnsignedByte().toInt()
                    textureFind = ShortArray(textures)
                    textureReplace = ShortArray(textures)
                    for (i in 0 until textures) {
                        textureFind!![i] = buffer.readShort()
                        textureReplace!![i] = buffer.readShort()
                    }
                }
                in 60..69 -> models[opcode - 60] = buffer.readUnsignedShort()
                else -> error(opcode)
            }
        }
    }

    override fun toString(): String {
        return "KitDefinition(colorFind=${Arrays.toString(colorFind)}, colorReplace=${Arrays.toString(colorReplace)}, textureFind=${Arrays.toString(textureFind)}, textureReplace=${Arrays.toString(textureReplace)}, bodyPartId=$bodyPartId, modelIds=${Arrays.toString(modelIds)}, models=${Arrays.toString(models)}, nonSelectable=$nonSelectable)"
    }

    class Loader(readableCache: ReadableCache) : DefinitionLoader.Record<KitDefinition>(readableCache, 2, 3) {
        override fun newDefinition() = KitDefinition()
    }
}