package com.runesuite.cache.content.def

import com.runesuite.cache.extensions.readString
import io.netty.buffer.ByteBuf

class ItemDefinition : CacheDefinition {

    var name: String = "null"
    var resizeX: Int = 128
    var resizeY: Int = 128
    var resizeZ: Int = 128
    var xan2d: Int = 0
    var yan2d: Int = 0
    var zan2d: Int = 0
    var cost: Int = 1
    var isTradeable: Boolean = false
    var stackable: Int = 0
    var inventoryModel: Int = 0
    var members: Boolean = false
    var colorFind: ShortArray? = null
    var colorReplace: ShortArray? = null
    var textureFind: ShortArray? = null
    var textureReplace: ShortArray? = null
    var zoom2d: Int = 200000
    var xOffset2d: Int = 0
    var yOffset2d: Int = 0
    var ambient: Int = 0
    var contrast: Int = 0
    var countCo: IntArray? = null
    var countObj: IntArray? = null
    var options: Array<String?> = arrayOf(null, null, "Take", null, null)
    var interfaceOptions: Array<String?> = arrayOf(null, null, null, null, "Drop")
    var maleModel0: Int = -1
    var maleModel1: Int = -1
    var maleModel2: Int = -1
    var maleOffset: Int = 0
    var maleHeadModel: Int = -1
    var maleHeadModel2: Int = -1
    var femaleModel0: Int = -1
    var femaleModel1: Int = -1
    var femaleModel2: Int = -1
    var femaleOffset: Int = 0
    var femaleHeadModel: Int = -1
    var femaleHeadModel2: Int = -1
    var notedID: Int = -1
    var notedTemplate: Int = -1
    var team: Int = 0
    var shiftClickDropIndex: Int = -2
    var boughtId: Int = -1
    var boughtTemplateId: Int = -1
    var placeholderId: Int = -1
    var placeholderTemplateId: Int = -1
    var params: MutableMap<Int, Any>? = null

    override fun read(buffer: ByteBuf) {
        while (true) {
            val opcode = buffer.readUnsignedByte().toInt()
            when (opcode) {
                0 -> return
                1 -> inventoryModel = buffer.readUnsignedShort()
                2 -> name = buffer.readString()
                4 -> zoom2d = buffer.readUnsignedShort()
                5 -> xan2d = buffer.readUnsignedShort()
                6 -> yan2d = buffer.readUnsignedShort()
                7 -> xOffset2d = buffer.readShort().toInt()
                8 -> yOffset2d = buffer.readShort().toInt()
                11 -> stackable = 1
                12 -> cost = buffer.readInt()
                16 -> members = true
                23 -> {
                    maleModel0 = buffer.readUnsignedShort()
                    maleOffset = buffer.readUnsignedByte().toInt()
                }
                24 -> maleModel1 = buffer.readUnsignedShort()
                25 -> {
                    femaleModel0 = buffer.readUnsignedShort()
                    femaleOffset = buffer.readUnsignedByte().toInt()
                }
                26 -> femaleModel1 = buffer.readUnsignedShort()
                in 30..34 -> options[opcode - 30] = buffer.readString().takeIf { it != "Hidden" }
                in 35..39 -> interfaceOptions[opcode - 35] = buffer.readString()
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
                42 -> shiftClickDropIndex = buffer.readUnsignedByte().toInt()
                65 -> isTradeable = true
                78 -> maleModel2 = buffer.readUnsignedShort()
                79 -> femaleModel2 = buffer.readUnsignedShort()
                90 -> maleHeadModel = buffer.readUnsignedShort()
                91 -> femaleHeadModel = buffer.readUnsignedShort()
                92 -> maleHeadModel2 = buffer.readUnsignedShort()
                93 -> femaleHeadModel2 = buffer.readUnsignedShort()
                95 -> zan2d = buffer.readUnsignedShort()
                97 -> notedID = buffer.readUnsignedShort()
                98 -> notedTemplate = buffer.readUnsignedShort()
                in 100..109 -> {
                    if (countObj == null) {
                        countObj = IntArray(10)
                        countCo = IntArray(10)
                    }
                    countObj!![opcode - 100] = buffer.readUnsignedShort()
                    countCo!![opcode - 100] = buffer.readUnsignedShort()
                }
                110 -> resizeX = buffer.readUnsignedShort()
                111 -> resizeY = buffer.readUnsignedShort()
                112 -> resizeZ = buffer.readUnsignedShort()
                113 -> ambient = buffer.readUnsignedByte().toInt()
                114 -> contrast = buffer.readUnsignedByte().toInt()
                115 -> team = buffer.readUnsignedByte().toInt()
                139 -> boughtId = buffer.readUnsignedShort()
                140 -> boughtTemplateId = buffer.readUnsignedShort()
                148 -> placeholderId = buffer.readUnsignedShort()
                149 -> placeholderTemplateId = buffer.readUnsignedShort()
                249 -> {
                    val length = buffer.readUnsignedByte().toInt()
                    params = HashMap<Int, Any>(length)
                    for (i in 0 until length) {
                        val isString = buffer.readUnsignedByte().toInt()
                        val key = buffer.readMedium()
                        val value: Any = when (isString) {
                            0 -> buffer.readInt()
                            1 -> buffer.readString()
                            else -> error(isString)
                        }
                        params!![key] = value
                    }
                }
                else -> error(opcode)
            }
        }
    }

    override fun toString(): String {
        return "ItemDefinition(name=$name)"
    }
}