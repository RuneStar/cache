//package com.runesuite.cache.content.def
//
//import io.netty.buffer.ByteBuf
//
//class VarbitDefinition : CacheDefinition() {
//
//    var index: Int = 0
//    var leastSignificantBit: Int = 0
//    var mostSignificantBit: Int = 0
//
//    override fun read(buffer: ByteBuf) {
//        while (true) {
//            val opcode = buffer.readUnsignedByte().toInt()
//            when (opcode) {
//                0 -> return
//                1 -> {
//                    index = buffer.readUnsignedShort()
//                    leastSignificantBit = buffer.readUnsignedByte().toInt()
//                    mostSignificantBit = buffer.readUnsignedByte().toInt()
//                }
//                else -> error(opcode)
//            }
//        }
//    }
//
//    override fun toString(): String {
//        return "VarbitDefinition(index=$index, leastSignificantBit=$leastSignificantBit, mostSignificantBit=$mostSignificantBit)"
//    }
//}