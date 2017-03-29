package com.runesuite.cache

import com.runesuite.cache.net.CacheClient

class Main {
}

fun main(args: Array<String>) {
    val cc = CacheClient(139, "oldschool7.runescape.com", 43594)
    cc.request(255, 255).get()
    cc.close()
}