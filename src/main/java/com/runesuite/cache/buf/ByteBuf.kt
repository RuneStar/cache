package com.runesuite.cache.buf

import io.netty.buffer.ByteBuf

internal fun ByteBuf.toCurrentList(): List<Byte> {
    return array().sliceArray(readerIndex() until writerIndex()).asList()
}