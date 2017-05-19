package com.runesuite.cache.content.def

import io.netty.buffer.ByteBuf

class UnderlayDefinition : CacheDefinition() {

    var color: Int = 0

    override fun read(buffer: ByteBuf) {
        while (true) {
            val opcode = buffer.readUnsignedByte().toInt()
            when (opcode) {
                0 -> return
                1 -> color = buffer.readUnsignedMedium()
                else -> error(opcode)
            }
        }
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeByte(1)
        buffer.writeMedium(color)
        buffer.writeByte(0)
    }

    override fun toString(): String {
        return "UnderlayDefinition(color=$color)"
    }
}