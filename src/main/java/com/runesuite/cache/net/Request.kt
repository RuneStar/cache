package com.runesuite.cache.net

import io.netty.buffer.ByteBuf

abstract class Request {
    abstract val byteBuf: ByteBuf
}