package com.runesuite.cache.extensions

import io.netty.util.AsciiString

fun String.toAscii(): AsciiString {
    return AsciiString(this)
}

fun ByteArray.asAscii(): AsciiString {
    return AsciiString(this, false)
}