package org.runestar.cache.format

import io.netty.buffer.ByteBuf

class Record(
        val buffer: ByteBuf,
        val nameHash: Int?
) {

    override fun toString(): String {
        return "Record(size=${buffer.readableBytes()}, nameHash=$nameHash)"
    }
}