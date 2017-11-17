//package com.runesuite.cache.content.def
//
//import com.hunterwb.kxtra.nettybuffer.bytebuf.readNullTerminatedString
//import com.runesuite.general.CHARSET
//import io.netty.buffer.ByteBuf
//
//class EnumDefinition : CacheDefinition() {
//
//    var intVals: IntArray? = null
//    var keyType: Char = ' '
//    var valType: Char = ' '
//    var defaultString = "null"
//    var defaultInt: Int = 0
//    var keys: IntArray? = null
//    var stringVals: Array<String>? = null
//
//    override fun read(buffer: ByteBuf) {
//        while (true) {
//            val opcode = buffer.readUnsignedByte().toInt()
//            when (opcode) {
//                0 -> return
//                1 -> keyType = buffer.readUnsignedByte().toChar()
//                2 -> valType = buffer.readUnsignedByte().toChar()
//                3 -> defaultString = buffer.readNullTerminatedString(CHARSET)
//                4 -> defaultInt = buffer.readInt()
//                5 -> {
//                    val length = buffer.readUnsignedShort()
//                    keys = IntArray(length)
//                    stringVals = Array(length) { "" }
//                    for (i in 0 until length) {
//                        keys!![i] = buffer.readInt()
//                        stringVals!![i] = buffer.readNullTerminatedString(CHARSET)
//                    }
//                }
//                6 -> {
//                    val length = buffer.readUnsignedShort()
//                    keys = IntArray(length)
//                    intVals = IntArray(length)
//                    for (i in 0 until length) {
//                        keys!![i] = buffer.readInt()
//                        intVals!![i] = buffer.readInt()
//                    }
//                }
//                else -> error(opcode)
//            }
//        }
//    }
//
//    override fun toString(): String {
//        return "EnumDefinition(keyType=$keyType, valType=$valType, defaultString=$defaultString, stringVals=${stringVals?.contentToString()})"
//    }
//}