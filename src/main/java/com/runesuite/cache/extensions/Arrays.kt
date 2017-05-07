package com.runesuite.cache.extensions

import java.nio.ByteBuffer

fun IntArray.asByteArray(): ByteArray {
    val b = ByteBuffer.allocate(size * Integer.BYTES)
    b.asIntBuffer().put(this)
    return b.array()
}