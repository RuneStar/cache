package com.runesuite.cache

import com.runesuite.cache.net.CacheClient

class Main {
}

fun main(args: Array<String>) {
    CacheClient(138, "oldschool7.runescape.com", 43594).use {
        it.request(255, 255).get()
    }
}