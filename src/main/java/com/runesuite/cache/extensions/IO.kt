package com.runesuite.cache.extensions

import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.SequenceInputStream

operator fun InputStream.plus(inputStream: InputStream): InputStream {
    return SequenceInputStream(this, inputStream)
}

fun Closeable.closeQuietly() {
    try {
        close()
    } catch (closeException: IOException) {

    }
}