package com.runesuite.cache.content.def

import io.netty.buffer.ByteBuf

class TrackDefinition : CacheDefinition() {

    var midi: ByteArray? = null

    override fun read(buffer: ByteBuf) {
        TODO("not implemented")
    }
}