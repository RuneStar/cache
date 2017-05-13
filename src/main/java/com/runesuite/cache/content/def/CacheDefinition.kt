package com.runesuite.cache.content.def

import io.netty.buffer.ByteBuf

interface CacheDefinition {

    fun read(buffer: ByteBuf)

    fun write(buffer: ByteBuf) {
        throw UnsupportedOperationException()
    }
}