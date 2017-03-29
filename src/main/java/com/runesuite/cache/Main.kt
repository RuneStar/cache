package com.runesuite.cache

import com.runesuite.cache.net.CacheClient

class Main {
}

fun main(args: Array<String>) {
    CacheClient(139, "oldschool7.runescape.com", 43594).use {
        it.request(255, 255).get()
        it.request(1, 0).get()
        it.request(3, 0).get()
    }
}