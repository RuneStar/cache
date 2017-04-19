package com.runesuite.cache

import io.netty.buffer.ByteBuf
import java.util.zip.CRC32

object Crc32 {

    fun checksum(bytes: ByteBuf): Int {
        val crc = CRC32()
        bytes.forEachByte {
            crc.update(it.toInt())
            true
        }
        return crc.value.toInt()
    }
}