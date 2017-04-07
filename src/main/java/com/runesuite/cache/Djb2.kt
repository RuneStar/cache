package com.runesuite.cache

object Djb2 {

    fun hash(string: String): Int {
        var hash = 0
        for (i in 0 until string.length) {
            hash = string[i].toInt() + ((hash shl 5) - hash)
        }
        return hash
    }
}