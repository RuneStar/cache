package org.runestar.cache.format

import io.netty.buffer.ByteBuf

object XteaCipher {

    // 128 bits
    const val KEY_SIZE = 4

    private const val PHI = -1640531527

    private const val ROUNDS = 32

    fun encrypt(buffer: ByteBuf, key: IntArray) {
        check(key.size == KEY_SIZE)
        if (key.all { it == 0 }) return
        buffer.markReaderIndex()
        repeat(buffer.readableBytes() / 8) {
            val startIndex = buffer.readerIndex()
            var v0 = buffer.readInt()
            var v1 = buffer.readInt()
            var sum = 0
            repeat(ROUNDS) {
                v0 += (((v1 shl 4) xor (v1 ushr 5)) + v1) xor (sum + key[sum and 3])
                sum += PHI
                v1 += (((v0 shl 4) xor (v0 ushr 5)) + v0) xor (sum + key[(sum ushr 11) and 3])
            }
            buffer.setInt(startIndex, v0)
            buffer.setInt(startIndex + 4, v1)
        }
        buffer.resetReaderIndex()
    }

    fun decrypt(buffer: ByteBuf, key: IntArray) {
        check(key.size == KEY_SIZE)
        if (key.all { it == 0 }) return
        buffer.markReaderIndex()
        repeat(buffer.readableBytes() / 8) {
            val startIndex = buffer.readerIndex()
            var v0 = buffer.readInt()
            var v1 = buffer.readInt()
            var sum = PHI * ROUNDS
            repeat(ROUNDS) {
                v1 -= (((v0 shl 4) xor (v0 ushr 5)) + v0) xor (sum + key[(sum ushr 11) and 3])
                sum -= PHI
                v0 -= (((v1 shl 4) xor (v1 ushr 5)) + v1) xor (sum + key[sum and 3])
            }
            buffer.setInt(startIndex, v0)
            buffer.setInt(startIndex + 4, v1)
        }
        buffer.resetReaderIndex()
    }
}