package com.runesuite.cache.buffer

import io.netty.buffer.ByteBuf

internal fun ByteBuf.asList(): List<Byte> {
    return array().asList()
}

