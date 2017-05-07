package com.runesuite.cache.extensions

import io.netty.util.AsciiString

fun CharSequence.toAscii(): AsciiString {
    String
    return AsciiString(this)
}

fun ByteArray.asAscii(): AsciiString {
    return AsciiString(this, false)
}

fun CharSequence.djb2(): Int {
    return fold(0) { acc, c -> c.toInt() + ((acc shl 5) - acc) }
}