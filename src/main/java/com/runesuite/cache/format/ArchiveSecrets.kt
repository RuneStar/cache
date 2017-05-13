package com.runesuite.cache.format

class ArchiveSecrets {

    private val ciphers: MutableMap<Pair<Int, Int>, XteaCipher?> = HashMap()

    operator fun get(index: Int, archive: Int) : XteaCipher? {
        return ciphers[index to archive]
    }

    operator fun set(index: Int, archive: Int, cipher: XteaCipher?) {
        ciphers[index to archive] = cipher
    }
}