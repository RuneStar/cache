package com.runesuite.cache.content.def

import com.hunterwb.kxtra.lang.short_.toUnsignedInt
import com.hunterwb.kxtra.nettybuffer.bytebuf.readNullTerminatedString
import com.runesuite.general.CHARSET
import io.netty.buffer.ByteBuf

abstract class CacheDefinition {

    abstract fun read(buffer: ByteBuf)
}

internal fun ByteBuf.readParams(): MutableMap<Int, Any> {
    val length = readUnsignedByte().toInt()
    val params = HashMap<Int, Any>(length)
    for (i in 0 until length) {
        val isString = readUnsignedByte().toInt()
        val key = readMedium()
        val value: Any = when (isString) {
            0 -> readInt()
            1 -> readNullTerminatedString(CHARSET)
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
        toUnsignedInt()
    }
}