package com.runesuite.cache.content.def

import com.hunterwb.kxtra.nettybuffer.bytebuf.readNullTerminatedString
import com.runesuite.general.CHARSET
import io.netty.buffer.ByteBuf

class ObjectDefinition : CacheDefinition() {

    var textureFind: ShortArray? = null
    var anInt2069 = 16
    var isSolid = false
    var name = "null"
    var objectModels: IntArray? = null
    var objectTypes: IntArray? = null
    var colorFind: ShortArray? = null
    var mapIconID = -1
    var textureReplace: ShortArray? = null
    var sizeX = 1
    var sizeY = 1
    var anInt2083 = 0
    var anIntArray2084: IntArray? = null
    var offsetX = 0
    var nonFlatShading = false
    var anInt2088 = -1
    var animationID = -1
    var varpID = -1
    var ambient = 0
    var contrast = 0
    var actions = arrayOfNulls<String>(5)
    var anInt2094 = 2
    var mapSceneID = -1
    var colorReplace: ShortArray? = null
    var aBool2097 = true
    var modelSizeX = 128
    var modelSizeHeight = 128
    var modelSizeY = 128
    var offsetHeight = 0
    var offsetY = 0
    var aBool2104 = false
    var anInt2105 = -1
    var anInt2106 = -1
    var configChangeDest: IntArray? = null
    var aBool2108 = false
    var configId = -1
    var anInt2110 = -1
    var aBool2111 = false
    var anInt2112 = 0
    var anInt2113 = 0
    var aBool2114 = true
    var params: MutableMap<Int, Any>? = null

    override fun read(buffer: ByteBuf) {
        while (true) {
            val opcode = buffer.readUnsignedByte().toInt()
            when (opcode) {
                0 -> return
                1 -> {
                    val length = buffer.readUnsignedByte().toInt()
                    if (length > 0) {
                        objectTypes = IntArray(length)
                        objectModels = IntArray(length)
                        for (index in 0 until length) {
                            objectModels!![index] = buffer.readUnsignedShort()
                            objectTypes!![index] = buffer.readUnsignedByte().toInt()
                        }
                    }
                }
                2 -> name = buffer.readNullTerminatedString(CHARSET)
                5 -> {
                    val length = buffer.readUnsignedByte().toInt()
                    if (length > 0) {
                        objectTypes = null
                        objectModels = IntArray(length) { buffer.readUnsignedShort() }
                    }
                }
                14 -> sizeX = buffer.readUnsignedByte().toInt()
                15 -> sizeY = buffer.readUnsignedByte().toInt()
                17 -> {
                    anInt2094 = 0
                    aBool2114 = false
                }
                18 -> aBool2114 = false
                19 -> anInt2088 = buffer.readUnsignedByte().toInt()
                21 -> anInt2105 = 0
                22 -> nonFlatShading = false
                23 -> aBool2111 = true
                24 -> animationID = buffer.readShort().toUnsignedN1()
                27 -> anInt2094 = 1
                28 -> anInt2069 = buffer.readUnsignedByte().toInt()
                29 -> ambient = buffer.readByte().toInt()
                39 -> contrast = buffer.readByte().toInt()
                in 30..34 -> actions[opcode - 30] = buffer.readNullTerminatedString(CHARSET).takeIf { it != "Hidden" }
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
                60 -> mapIconID = buffer.readUnsignedShort()
                62 -> aBool2108 = true
                64 -> aBool2097 = false
                65 -> modelSizeX = buffer.readUnsignedShort()
                66 -> modelSizeHeight = buffer.readUnsignedShort()
                67 -> modelSizeY = buffer.readUnsignedShort()
                68 -> mapSceneID = buffer.readUnsignedShort()
                69 -> buffer.skipBytes(1)
                70 -> offsetX = buffer.readUnsignedShort()
                71 -> offsetHeight = buffer.readUnsignedShort()
                72 -> offsetY = buffer.readUnsignedShort()
                73 -> aBool2104 = true
                74 -> isSolid = true
                75 -> anInt2106 = buffer.readUnsignedByte().toInt()
                77 -> {
                    varpID = buffer.readShort().toUnsignedN1()
                    configId = buffer.readShort().toUnsignedN1()
                    val length = buffer.readUnsignedByte().toInt()
                    configChangeDest = IntArray(length + 2) {
                        if (it == length + 1) -1
                        else buffer.readShort().toUnsignedN1()
                    }
                }
                78 -> {
                    anInt2110 = buffer.readUnsignedShort()
                    anInt2083 = buffer.readUnsignedByte().toInt()
                }
                79 -> {
                    anInt2112 = buffer.readUnsignedShort()
                    anInt2113 = buffer.readUnsignedShort()
                    anInt2083 = buffer.readUnsignedByte().toInt()
                    val length = buffer.readUnsignedByte().toInt()
                    anIntArray2084 = IntArray(length) { buffer.readUnsignedShort() }
                }
                81 -> anInt2105 = buffer.readUnsignedByte().toInt()
                82 -> {}
                92 -> {
                    varpID = buffer.readShort().toUnsignedN1()
                    configId = buffer.readShort().toUnsignedN1()
                    val v = buffer.readShort().toUnsignedN1()
                    val length = buffer.readUnsignedByte().toInt()
                    configChangeDest = IntArray(length + 2) {
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
        return "ObjectDefinition(name=$name)"
    }
}