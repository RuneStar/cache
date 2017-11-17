//package com.runesuite.cache.content.def
//
//import com.hunterwb.kxtra.nettybuffer.bytebuf.readNullTerminatedString
//import com.runesuite.general.CHARSET
//import io.netty.buffer.ByteBuf
//import java.util.*
//
//class ScriptDefinition : CacheDefinition() {
//
//    var intStackCount: Int = 0
//    var instructions: IntArray? = null
//    var intOperands: IntArray? = null
//    var stringOperands: Array<String?>? = null
//    var localStringCount: Int = 0
//    var stringStackCount: Int = 0
//    var localIntCount: Int = 0
//    var switches: Array<MutableMap<Int, Int>>? = null
//
//    override fun read(buffer: ByteBuf) {
//        val endOffset = buffer.getUnsignedShort(buffer.writerIndex() - 2)
//        buffer.markReaderIndex()
//        buffer.readerIndex(buffer.writerIndex() - 2 - endOffset - 12)
//        val paramCount = buffer.readInt()
//        localIntCount = buffer.readUnsignedShort()
//        localStringCount = buffer.readUnsignedShort()
//        intStackCount = buffer.readUnsignedShort()
//        stringStackCount = buffer.readUnsignedShort()
//        val numSwitches = buffer.readUnsignedByte().toInt()
//        if (numSwitches > 0) {
//            switches = Array(numSwitches) {
//                val count = buffer.readUnsignedShort()
//                LinkedHashMap<Int, Int>(count).apply {
//                    repeat(count) {
//                        val key = buffer.readInt()
//                        val pcOffset = buffer.readInt()
//                        this[key] = pcOffset
//                    }
//                }
//            }
//        }
//        buffer.resetReaderIndex()
//        buffer.readNullTerminatedString(CHARSET)
//        instructions = IntArray(paramCount)
//        intOperands = IntArray(paramCount)
//        stringOperands = Array(paramCount) { null }
//        while (buffer.readerIndex() < buffer.writerIndex() - 12) {
//            var i = 0
//            val insn = buffer.readUnsignedShort()
//            if (insn == 3) {
//                stringOperands!![i] = buffer.readNullTerminatedString(CHARSET)
//            } else if (insn < 100 && insn != 21 && insn != 38 && insn != 39) {
//                intOperands!![i] = buffer.readInt()
//            } else {
//                intOperands!![i] = buffer.readUnsignedByte().toInt()
//            }
//            instructions!![i] = insn
//            i += 1
//        }
//    }
//
//    override fun toString(): String {
//        return "ScriptDefinition(strings=${stringOperands?.filterNotNull()}, switches=${switches?.contentToString()})"
//    }
//}