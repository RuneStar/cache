package com.runesuite.cache.format

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.bouncycastle.crypto.engines.XTEAEngine
import org.bouncycastle.crypto.params.KeyParameter
import org.kxtra.lang.intarray.asByteArray
import org.kxtra.netty.buffer.bytebuf.readArray

class XteaCipher {

    companion object {
        private const val BLOCK_SIZE = 8

        // 128 bits
        const val KEY_SIZE = 4
    }

    private val engine = XTEAEngine()

    fun encrypt(buffer: ByteBuf, key: IntArray?): ByteBuf {
        return process(buffer, key, forEncryption = true)
    }

    fun decrypt(buffer: ByteBuf, key: IntArray?): ByteBuf {
        return process(buffer, key, forEncryption = false)
    }

    private fun process(buffer: ByteBuf, key: IntArray?, forEncryption: Boolean): ByteBuf {
        if (key == null) return buffer.retainedDuplicate()
        require(key.size == KEY_SIZE) { "incorrect key size; expected $KEY_SIZE but got ${key.size}" }
        if (key.all { it == 0 }) return buffer.retainedDuplicate()
        val size = buffer.readableBytes()
        val processSize = size - (size % BLOCK_SIZE)
        check(processSize in 0..size)
        val out = ByteArray(size)
        buffer.markReaderIndex()
        if (processSize > 0) {
            engine.init(forEncryption, KeyParameter(key.asByteArray()))
            val processIn = buffer.readArray(processSize)
            engine.processBlock(processIn, 0, out, 0)
        }
        if (processSize < size) {
            buffer.readBytes(out, processSize, size - processSize)
        }
        buffer.resetReaderIndex()
        return Unpooled.wrappedBuffer(out)
    }
}