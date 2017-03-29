package com.runesuite.cache.net

import io.netty.buffer.ByteBuf

abstract class Request {

    abstract fun write(output: ByteBuf)
}