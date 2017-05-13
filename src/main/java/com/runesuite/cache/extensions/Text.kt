package com.runesuite.cache.extensions

import io.netty.util.AsciiString

fun CharSequence.toAscii(): AsciiString {
    return AsciiString(this)
}

fun ByteArray.asAscii(): AsciiString {
    return AsciiString(this, false)
}

fun CharSequence.djb2(): Int {
    return fold(0) { acc, c -> c.toInt() + ((acc shl 5) - acc) }
}

private val RS_ASCII_EXTENSION = charArrayOf(
        '\u20ac', '\u0000', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021', '\u02c6', '\u2030', '\u0160',
        '\u2039', '\u0152', '\u0000', '\u017d', '\u0000', '\u0000', '\u2018', '\u2019', '\u201c', '\u201d', '\u2022',
        '\u2013', '\u2014', '\u02dc', '\u2122', '\u0161', '\u203a', '\u0153', '\u0000', '\u017e', '\u0178')

internal fun Byte.toRsChar(): Char {
    val x = this.toUnsigned() - 128
    return if (x in RS_ASCII_EXTENSION.indices) {
        RS_ASCII_EXTENSION[x].takeIf { it != '\u0000' } ?: '?'
    } else {
        toChar()
    }
}