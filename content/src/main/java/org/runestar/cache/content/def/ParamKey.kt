package org.runestar.cache.content.def

import io.netty.buffer.ByteBuf
import org.runestar.cache.content.load.DefinitionLoader
import org.runestar.cache.format.ReadableCache

class ParamKey : CacheDefinition() {

    var b = true
    var type: Char = 0.toChar()
    var keyInt = 0
    var keyString: String? = null

    override fun read(buffer: ByteBuf) {
        while (true) {
            val opcode = buffer.readUnsignedByte().toInt()
            when (opcode) {
                1 -> type = buffer.readByte().toChar()
                2 -> keyInt = buffer.readInt()
                4 -> b = false
                5 -> keyString = buffer.readNullTerminatedString()
                0 -> return
            }
        }
    }

    class Loader(readableCache: ReadableCache) : DefinitionLoader.Record<ParamKey>(readableCache, 2, 11) {
        override fun newDefinition() = ParamKey()
    }
}