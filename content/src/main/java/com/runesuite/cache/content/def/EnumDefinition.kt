package com.runesuite.cache.content.def

import com.runesuite.cache.content.load.DefinitionLoader
import com.runesuite.cache.format.ReadableCache
import io.netty.buffer.ByteBuf

class EnumDefinition : CacheDefinition() {

    var intVals: IntArray? = null
    var keyType: Char = 0.toChar()
    var valType: Char = 0.toChar()
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
                3 -> defaultString = buffer.readNullTerminatedString()
                4 -> defaultInt = buffer.readInt()
                5 -> {
                    val length = buffer.readUnsignedShort()
                    keys = IntArray(length)
                    stringVals = Array(length) { "" }
                    for (i in 0 until length) {
                        keys!![i] = buffer.readInt()
                        stringVals!![i] = buffer.readNullTerminatedString()
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
        return "EnumDefinition(" +
                "keyType=$keyType, " +
                "valType=$valType, " +
                "defaultString=$defaultString, " +
                "defaultInt=$defaultInt, " +
                "keys=${keys?.contentToString()}, " +
                "stringVals=${stringVals?.contentToString()}, " +
                "intVals=${intVals?.contentToString()})"
    }

    class Loader(readableCache: ReadableCache) : DefinitionLoader.Record<EnumDefinition>(readableCache, 2, 8) {
        override fun newDefinition() = EnumDefinition()
    }
}