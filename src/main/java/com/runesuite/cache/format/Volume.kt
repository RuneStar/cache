package com.runesuite.cache.format

import io.netty.buffer.ByteBuf

interface Volume {

    val compressor: Compressor

    val compressed: ByteBuf

    val decompressed: ByteBuf

    val crc: Int

    val version: Int?
}