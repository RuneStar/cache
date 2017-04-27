package com.runesuite.cache.format

import com.runesuite.cache.extensions.update
import io.netty.buffer.ByteBuf
import java.util.zip.CRC32

object Crc32 {

    fun checksum(bytes: ByteBuf): Int {
        val crc = CRC32()
        crc.update(bytes)
        return crc.value.toInt()
    }
}