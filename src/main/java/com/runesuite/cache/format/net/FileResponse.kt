package com.runesuite.cache.format.net

import com.runesuite.cache.format.CompressedVolume
import com.runesuite.cache.format.Volume
import io.netty.buffer.ByteBuf

class FileResponse(override val input: ByteBuf) : Response(input) {

    companion object {
        const val HEADER_LENGTH = java.lang.Byte.BYTES + java.lang.Short.BYTES
    }

    val index = input.getUnsignedByte(0).toInt()

    val archive = input.getUnsignedShort(1)

    private val archiveSlice = input.duplicate().skipBytes(HEADER_LENGTH)

    val done = CompressedVolume.isValid(archiveSlice)

    val data: Volume by lazy {
        check(done)
        CompressedVolume(archiveSlice)
    }

    override fun toString(): String {
        return "FileResponse(index=$index, archive=$archive, done=$done)"
    }
}