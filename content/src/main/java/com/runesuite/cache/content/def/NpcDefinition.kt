package com.runesuite.cache.content.def

import com.runesuite.cache.content.load.RecordDefinitionLoader
import com.runesuite.cache.format.ReadableCache
import io.netty.buffer.ByteBuf

class NpcDefinition : CacheDefinition() {

    var colorFind: ShortArray? = null
    var anInt2156 = 32
    var name = "null"
    var colorReplace: ShortArray? = null
    var models: IntArray? = null
    var models_2: IntArray? = null
    var stanceAnimation = -1
    var anInt2165 = -1
    var tileSpacesOccupied = 1
    var walkAnimation = -1
    var textureReplace: ShortArray? = null
    var rotate90RightAnimation = -1
    var aBool2170 = true
    var resizeX = 128
    var contrast = 0
    var rotate180Animation = -1
    var anInt2174 = -1
    var options = arrayOfNulls<String>(5)
    var renderOnMinimap = true
    var combatLevel = -1
    var rotate90LeftAnimation = -1
    var resizeY = 128
    var hasRenderPriority = false
    var ambient = 0
    var headIcon = -1
    var anIntArray2185: IntArray? = null
    var textureFind: ShortArray? = null
    var anInt2187 = -1
    var isClickable = true
    var anInt2189 = -1
    var aBool2190 = false
    var params: MutableMap<Int, Any>? = null

    override fun read(buffer: ByteBuf) {
        while (true) {
            val opcode = buffer.readUnsignedByte().toInt()
            when (opcode) {
                0 -> return
                1 -> {
                    val length = buffer.readUnsignedByte().toInt()
                    models = IntArray(length) { buffer.readUnsignedShort() }
                }
                2 -> name = buffer.readNullTerminatedString()
                12 -> tileSpacesOccupied = buffer.readUnsignedByte().toInt()
                13 -> stanceAnimation = buffer.readUnsignedShort()
                14 -> walkAnimation = buffer.readUnsignedShort()
                15 -> anInt2165 = buffer.readUnsignedShort()
                16 -> anInt2189 = buffer.readUnsignedShort()
                17 -> {
                    walkAnimation = buffer.readUnsignedShort()
                    rotate180Animation = buffer.readUnsignedShort()
                    rotate90RightAnimation = buffer.readUnsignedShort()
                    rotate90LeftAnimation = buffer.readUnsignedShort()
                }
                in 30..34 -> options[opcode - 30] = buffer.readNullTerminatedString().takeIf { it != "Hidden" }
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
                60 -> {
                    val length = buffer.readUnsignedByte().toInt()
                    models_2 = IntArray(length) { buffer.readUnsignedShort() }
                }
                93 -> renderOnMinimap = false
                95 -> combatLevel = buffer.readUnsignedShort()
                97 -> resizeX = buffer.readUnsignedShort()
                98 -> resizeY = buffer.readUnsignedShort()
                99 -> hasRenderPriority = true
                100 -> ambient = buffer.readByte().toInt()
                101 -> contrast = buffer.readByte().toInt()
                102 -> headIcon = buffer.readUnsignedShort()
                103 -> anInt2156 = buffer.readUnsignedShort()
                106 -> {
                    anInt2174 = buffer.readShort().toUnsignedN1()
                    anInt2187 = buffer.readShort().toUnsignedN1()
                    val length = buffer.readUnsignedByte().toInt()
                    anIntArray2185 = IntArray(length + 2) {
                        if (it == length + 1) -1
                        else buffer.readShort().toUnsignedN1()
                    }
                }
                107 -> isClickable = false
                109 -> aBool2170 = false
                111 -> aBool2190 = true
                118 -> {
                    anInt2174 = buffer.readShort().toUnsignedN1()
                    anInt2187 = buffer.readShort().toUnsignedN1()
                    val v = buffer.readUnsignedShort()
                    val length = buffer.readUnsignedByte().toInt()
                    anIntArray2185 = IntArray(length + 2) {
                        if (it == length + 1) v
                        else buffer.readShort().toUnsignedN1()
                    }
                }
                249 -> params = buffer.readParams()
                else -> error(opcode)
            }
        }
    }

    override fun toString(): String {
        return "NpcDefinition(name=$name)"
    }

    class Loader(readableCache: ReadableCache) : RecordDefinitionLoader<NpcDefinition>(readableCache, 2, 9) {
        override fun newDefinition() = NpcDefinition()
    }
}