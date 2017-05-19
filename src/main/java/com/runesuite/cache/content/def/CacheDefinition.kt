package com.runesuite.cache.content.def

import com.runesuite.cache.extensions.readString
import com.runesuite.cache.extensions.toUnsigned
import io.netty.buffer.ByteBuf

abstract class CacheDefinition {

    var id: Int = -1

    abstract fun read(buffer: ByteBuf)

    open fun write(buffer: ByteBuf) {
        throw UnsupportedOperationException()
    }
}

internal fun ByteBuf.readParams(): MutableMap<Int, Any> {
    val length = readUnsignedByte().toInt()
    val params = HashMap<Int, Any>(length)
    for (i in 0 until length) {
        val isString = readUnsignedByte().toInt()
        val key = readMedium()
        val value: Any = when (isString) {
            0 -> readInt()
            1 -> readString()
            else -> error(isString)
        }
        params[key] = value
    }
    return params
}

internal fun Short.toUnsignedN1(): Int {
    return if (toInt() == -1) {
        -1
    } else {
        toUnsigned()
    }
}