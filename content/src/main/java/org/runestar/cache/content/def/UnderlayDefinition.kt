package org.runestar.cache.content.def

import io.netty.buffer.ByteBuf
import org.runestar.cache.content.load.DefinitionLoader
import org.runestar.cache.format.ReadableCache

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

    override fun toString(): String {
        return "UnderlayDefinition(color=$color)"
    }

    class Loader(readableCache: ReadableCache) : DefinitionLoader.Record<UnderlayDefinition>(readableCache, 2, 1) {
        override fun newDefinition() = UnderlayDefinition()
    }
}