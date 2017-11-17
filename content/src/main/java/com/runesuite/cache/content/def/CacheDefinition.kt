package com.runesuite.cache.content.def

import io.netty.buffer.ByteBuf

abstract class CacheDefinition {

    abstract fun read(buffer: ByteBuf)
}

