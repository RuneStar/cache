package org.runestar.cache.content.def

import org.runestar.cache.content.load.DefinitionLoader
import org.runestar.cache.format.ReadableCache
import io.netty.buffer.ByteBuf

class VarbitDefinition : CacheDefinition() {

    var index: Int = 0
    var first: Int = 0
    var last: Int = 0

    override fun read(buffer: ByteBuf) {
        while (true) {
            val opcode = buffer.readUnsignedByte().toInt()
            when (opcode) {
                0 -> return
                1 -> {
                    index = buffer.readUnsignedShort()
                    first = buffer.readUnsignedByte().toInt()
                    last = buffer.readUnsignedByte().toInt()
                }
                else -> error(opcode)
            }
        }
    }

    override fun toString(): String {
        return "VarbitDefinition(index=$index, first=$first, last=$last)"
    }

    class Loader(readableCache: ReadableCache) : DefinitionLoader.Record<VarbitDefinition>(readableCache, 2, 14) {
        override fun newDefinition() = VarbitDefinition()
    }
}