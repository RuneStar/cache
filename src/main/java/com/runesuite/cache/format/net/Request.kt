package com.runesuite.cache.format.net

import io.netty.buffer.ByteBuf

internal abstract class Request {

    abstract fun write(output: ByteBuf)
}