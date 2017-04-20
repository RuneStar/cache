package com.runesuite.cache.extensions

import java.nio.MappedByteBuffer

fun MappedByteBuffer.unmap() {
    // http://stackoverflow.com/a/19447758
    try {
        val cleaner = this.javaClass.getMethod("cleaner")
        cleaner.isAccessible = true
        val clean = Class.forName("sun.misc.Cleaner").getMethod("clean")
        clean.isAccessible = true
        clean.invoke(cleaner.invoke(this))
    } catch (e: Exception) {
        throw e
        // todo
    }
}