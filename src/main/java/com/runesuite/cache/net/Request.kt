package com.runesuite.cache.net

import io.netty.buffer.ByteBuf

internal abstract class Request {

    abstract fun write(output: ByteBuf)
}