package com.runesuite.cache.extensions

import java.nio.ByteBuffer

fun Byte.toUnsigned(): Int {
    return java.lang.Byte.toUnsignedInt(this)
}

fun Short.toUnsigned(): Int {
    return java.lang.Short.toUnsignedInt(this)
}

inline fun ByteArray.setEach(value: (Int) -> Byte) {
    indices.forEach {
        set(it, value.invoke(it))
    }
}

fun IntArray.asByteArray(): ByteArray {
    val b = ByteBuffer.allocate(size * Integer.BYTES)
    b.asIntBuffer().put(this)
    return b.array()
}