package com.runesuite.cache.format

import io.netty.buffer.ByteBuf
import org.bouncycastle.jcajce.provider.digest.Whirlpool
import java.security.MessageDigest

object Whirlpool {

    private val messageDigest: MessageDigest = Whirlpool.Digest()

    const val HASH_LENGTH = 64

    @Synchronized
    fun hash(bytes: ByteBuf): ByteArray {
        // org.bouncycastle.crypto.digests.WhirlPoolDigest.update(byte[] in, int inOff, int len)
        bytes.forEachByte {
            messageDigest.update(it)
            true
        }
        val hash = messageDigest.digest()
        check(hash.size == HASH_LENGTH)
        return hash
    }
}