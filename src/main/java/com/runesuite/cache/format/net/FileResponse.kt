package com.runesuite.cache.format.net

import com.runesuite.cache.format.Container
import com.runesuite.cache.format.DefaultContainer
import io.netty.buffer.ByteBuf

class FileResponse(override val input: ByteBuf) : Response(input) {

    companion object {
        const val HEADER_LENGTH = java.lang.Byte.BYTES + java.lang.Short.BYTES
    }

    val index = input.getUnsignedByte(0).toInt()

    val archive = input.getUnsignedShort(1)

    private val archiveSlice = input.duplicate().skipBytes(HEADER_LENGTH)

    val done = DefaultContainer.isValid(archiveSlice)

    val data: Container by lazy {
        check(done)
        DefaultContainer(archiveSlice)
    }

    override fun toString(): String {
        return "FileResponse(index=$index, archive=$archive, done=$done)"
    }
}