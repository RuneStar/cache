package com.runesuite.cache

import io.netty.buffer.ByteBuf

object Whirlpool {

    val messageDigest = org.bouncycastle.jcajce.provider.digest.Whirlpool.Digest()

    @Synchronized
    fun hash(bytes: ByteBuf): ByteArray {
        bytes.forEachByte {
            messageDigest.update(it)
            true
        }
        return messageDigest.digest()
    }
}