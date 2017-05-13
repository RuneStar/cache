package com.runesuite.cache.format

import com.runesuite.cache.extensions.asByteArray
import com.runesuite.cache.extensions.readArray
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.bouncycastle.crypto.engines.XTEAEngine
import org.bouncycastle.crypto.params.KeyParameter

class XteaCipher(val key: IntArray) {

    companion object {
        private const val BLOCK_SIZE = 8

        // 128 bits
        const val KEY_SIZE = 4
    }

    private val isEmpty = key.all { it == 0 }

    private val engine by lazy { XTEAEngine() }

    private val keyParam by lazy { KeyParameter(key.asByteArray()) }

    init {
        require(key.size == KEY_SIZE) { "Key size (${key.size}) must be $KEY_SIZE" }
    }

    fun encrypt(buffer: ByteBuf): ByteBuf {
        return process(buffer, forEncryption = true)
    }

    fun decrypt(buffer: ByteBuf): ByteBuf {
        return process(buffer, forEncryption = false)
    }

    private fun process(buffer: ByteBuf, forEncryption: Boolean): ByteBuf {
        if (isEmpty) {
            return buffer.retainedDuplicate()
        }
        val size = buffer.readableBytes()
        val processSize = size - (size % BLOCK_SIZE)
        check(processSize in 0..size)
        val out = ByteArray(size)
        buffer.markReaderIndex()
        if (processSize > 0) {
            engine.init(forEncryption, keyParam)
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