package com.runesuite.cache

import com.runesuite.cache.extensions.readableArray
import io.netty.buffer.ByteBuf
import java.util.zip.CRC32

object Crc32 {

    fun checksum(bytes: ByteBuf): Int {
        val crc = CRC32()
        if (bytes.nioBufferCount() >= 1) {
            bytes.nioBuffers().forEach {
                crc.update(it)
            }
        } else {
            val array = bytes.readableArray()
            crc.update(array)
        }
        return crc.value.toInt()
    }
}