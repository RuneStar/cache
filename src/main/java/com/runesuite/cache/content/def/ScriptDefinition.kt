package com.runesuite.cache.content.def

import com.runesuite.cache.extensions.readString
import com.runesuite.cache.extensions.readableArray
import io.netty.buffer.ByteBuf

class ScriptDefinition : CacheDefinition() {

    var intStackCount: Int = 0
    var instructions: IntArray? = null
    var intOperands: IntArray? = null
    var stringOperands: Array<String?>? = null
    var localStringCount: Int = 0
    var stringStackCount: Int = 0
    var localIntCount: Int = 0
    var switches: Array<Map<Int, Int>>? = null

    override fun read(buffer: ByteBuf) {
        println(buffer.readableArray().contentToString())
        return
        buffer.markReaderIndex()
        buffer.readerIndex(buffer.writerIndex() - 12)
        val paramCount = buffer.readInt()
        println(paramCount)
        localIntCount = buffer.readUnsignedShort()
        localStringCount = buffer.readUnsignedShort()
        intStackCount = buffer.readUnsignedShort()
        stringStackCount = buffer.readUnsignedShort()
        buffer.resetReaderIndex()
        buffer.readString()
        instructions = IntArray(paramCount)
        intOperands = IntArray(paramCount)
        stringOperands = Array(paramCount) { null }
        while (buffer.readerIndex() < buffer.writerIndex() - 12) {
            var i = 0
            val insn = buffer.readUnsignedShort()
            if (insn == 3) {
                stringOperands!![i] = buffer.readString()
            } else if (insn < 100 && insn != 21 && insn != 38 && insn != 39) {
                intOperands!![i] = buffer.readInt()
            } else {
                intOperands!![i] = buffer.readUnsignedByte().toInt()
            }
            instructions!![i] = insn
            i += 1
        }
        buffer.skipBytes(buffer.readableBytes())
    }

    override fun toString(): String {
        return "ScriptDefinition(strings=${stringOperands?.filterNotNull()})"
    }
}