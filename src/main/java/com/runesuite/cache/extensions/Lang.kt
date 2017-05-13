package com.runesuite.cache.extensions

import java.nio.ByteBuffer

fun Byte.toUnsigned(): Int {
    return java.lang.Byte.toUnsignedInt(this)
}

fun Short.toUnsigned(): Int {
    return java.lang.Short.toUnsignedInt(this)
}

internal fun Short.toUnsignedN1(): Int {
    return if (toInt() == -1) {
        -1
    } else {
        toUnsigned()
    }
}

fun IntArray.asByteArray(): ByteArray {
    val b = ByteBuffer.allocate(size * Integer.BYTES)
    b.asIntBuffer().put(this)
    return b.array()
}