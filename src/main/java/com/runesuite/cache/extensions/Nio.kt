package com.runesuite.cache.extensions

import io.netty.util.internal.PlatformDependent
import java.nio.ByteBuffer

fun ByteBuffer.freeDirect() {
    check(isDirect)
    PlatformDependent.freeDirectBuffer(this)
}