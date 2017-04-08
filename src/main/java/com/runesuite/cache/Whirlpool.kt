package com.runesuite.cache

import io.netty.buffer.ByteBuf

object Whirlpool {

    private val messageDigest = org.bouncycastle.jcajce.provider.digest.Whirlpool.Digest()

    const val HASH_LENGTH = 64

    @Synchronized
    fun hash(bytes: ByteBuf): ByteArray {
        bytes.forEachByte {
            messageDigest.update(it)
            true
        }
        val hash = messageDigest.digest()
        check(hash.size == HASH_LENGTH)
        return hash
    }
}