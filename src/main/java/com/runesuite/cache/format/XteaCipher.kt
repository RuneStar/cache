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
        const val KEY_SIZE = 4
    }

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
        val size = buffer.readableBytes()
        val processSize = size - (size % BLOCK_SIZE)
        val processOut = ByteArray(size)
        buffer.markReaderIndex()
        if (processSize > 0) {
            val xteaEngine = XTEAEngine()
            xteaEngine.init(forEncryption, KeyParameter(key.asByteArray()))
            val processIn = buffer.readArray(processSize)
            xteaEngine.processBlock(processIn, 0, processOut, 0)
        }
        if (processSize < size) {
            buffer.readBytes(processOut, processSize, size - processSize)
        }
        buffer.resetReaderIndex()
        return Unpooled.wrappedBuffer(processOut)
    }
}