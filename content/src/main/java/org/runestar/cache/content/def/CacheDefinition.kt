package org.runestar.cache.content.def

import io.netty.buffer.ByteBuf

abstract class CacheDefinition {

    abstract fun read(buffer: ByteBuf)
}

