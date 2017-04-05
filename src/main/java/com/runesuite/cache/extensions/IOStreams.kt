package com.runesuite.cache.extensions

import java.io.InputStream
import java.io.SequenceInputStream

operator fun InputStream.plus(inputStream: InputStream): InputStream {
    return SequenceInputStream(this, inputStream)
}