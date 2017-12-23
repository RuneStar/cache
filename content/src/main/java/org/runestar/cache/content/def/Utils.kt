package org.runestar.cache.content.def

import org.runestar.general.CHARSET
import io.netty.buffer.ByteBuf
import org.kxtra.lang.short_.toUnsignedInt

internal fun Short.toUnsignedN1(): Int {
    return if (toInt() == -1) {
        -1
    } else {
        toUnsignedInt()
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
            1 -> readNullTerminatedString()
            else -> error(isString)
        }
        params[key] = value
    }
    return params
}

internal fun ByteBuf.readNullTerminatedString(): String {
    val length = bytesBefore(0)
    check(length != -1)
    return toString(readerIndex(), length, CHARSET).also { skipBytes(length + 1) }
}