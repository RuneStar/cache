package com.runesuite.cache.content.def

import com.fasterxml.jackson.annotation.JsonInclude
import io.netty.buffer.ByteBuf

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
class SpotAnimDefinition : CacheDefinition() {

    var rotaton = 0
    var textureReplace: ShortArray? = null
    var textureFind: ShortArray? = null
    var resizeY = 128
    var animationId = -1
    var colorFind: ShortArray? = null
    var colorReplace: ShortArray? = null
    var resizeX = 128
    var modelId: Int = 0
    var ambient = 0
    var contrast = 0

    override fun read(buffer: ByteBuf) {
        while (true) {
            val opcode = buffer.readUnsignedByte().toInt()
            when (opcode) {
                0 -> return
                1 -> modelId = buffer.readUnsignedShort()
                2 -> animationId = buffer.readUnsignedShort()
                4 -> resizeX = buffer.readUnsignedShort()
                5 -> resizeY = buffer.readUnsignedShort()
                6 -> rotaton = buffer.readUnsignedShort()
                7 -> ambient = buffer.readUnsignedByte().toInt()
                8 -> contrast = buffer.readUnsignedByte().toInt()
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
                else -> error(opcode)
            }
        }
    }

    override fun toString(): String {
        return "SpotAnimDefinition(animationId=$animationId, modelId=$modelId)"
    }
}