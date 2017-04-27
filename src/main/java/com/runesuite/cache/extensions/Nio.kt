package com.runesuite.cache.extensions

import io.netty.util.internal.PlatformDependent
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

fun ByteBuffer.freeDirect() {
    check(isDirect)
    PlatformDependent.freeDirectBuffer(this)
}

fun ShortBuffer.getUnsigned(index: Int): Int {
    return java.lang.Short.toUnsignedInt(get(index))
}

fun IntBuffer.getUnsigned(index: Int): Long {
    return Integer.toUnsignedLong(get(index))
}