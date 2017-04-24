package com.runesuite.cache.format

object Djb2 {

    fun hash(string: String): Int {
        var hash = 0
        string.forEach {
            hash = it.toInt() + ((hash shl 5) - hash)
        }
        return hash
    }
}