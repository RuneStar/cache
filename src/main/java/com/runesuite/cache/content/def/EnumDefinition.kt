package com.runesuite.cache.content.def

import com.runesuite.cache.extensions.readString
import io.netty.buffer.ByteBuf

class EnumDefinition : CacheDefinition() {

    var intVals: IntArray? = null
    var keyType: Char = ' '
    var valType: Char = ' '
    var defaultString = "null"
    var defaultInt: Int = 0
    var keys: IntArray? = null
    var stringVals: Array<String>? = null

    override fun read(buffer: ByteBuf) {
        while (true) {
            val opcode = buffer.readUnsignedByte().toInt()
            when (opcode) {
                0 -> return
                1 -> keyType = buffer.readUnsignedByte().toChar()
                2 -> valType = buffer.readUnsignedByte().toChar()
                3 -> defaultString = buffer.readString()
                4 -> defaultInt = buffer.readInt()
                5 -> {
                    val length = buffer.readUnsignedShort()
                    keys = IntArray(length)
                    stringVals = Array(length) { "" }
                    for (i in 0 until length) {
                        keys!![i] = buffer.readInt()
                        stringVals!![i] = buffer.readString()
                    }
                }
                6 -> {
                    val length = buffer.readUnsignedShort()
                    keys = IntArray(length)
                    intVals = IntArray(length)
                    for (i in 0 until length) {
                        keys!![i] = buffer.readInt()
                        intVals!![i] = buffer.readInt()
                    }
                }
                else -> error(opcode)
            }
        }
    }

    override fun toString(): String {
        return "EnumDefinition(keyType=$keyType, valType=$valType, defaultString=$defaultString, stringVals=${stringVals?.contentToString()})"
    }
}