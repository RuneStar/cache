package com.runesuite.cache.format

import com.runesuite.cache.extensions.readSliceAsInts
import com.runesuite.cache.extensions.readSliceAsShorts
import io.netty.buffer.ByteBuf
import java.nio.IntBuffer

data class IndexReference(
        val format: Int,
        val version: Int,
        val flags: Int,
        val archives: List<ArchiveInfo?>
) {

    companion object {

        fun read(buffer: ByteBuf): IndexReference {
            val format = buffer.readUnsignedByte().toInt()
            check(format in 5..6)
            val version = if (format >= 6) buffer.readInt() else 0
            val flags = buffer.readUnsignedByte().toInt()
            val entriesCount = buffer.readUnsignedShort()
            val entryIds = IntArray(entriesCount)
            var accumulator = 0
            for (i in 0 until entriesCount) {
                val delta = buffer.readUnsignedShort()
                accumulator += delta
                entryIds[i] = accumulator
            }

            val entryIdentifiers: IntBuffer? = if (flags and Flag.IDENTIFIERS.id != 0) {
                buffer.readSliceAsInts(entriesCount)
            } else null

            val entryCrcs = buffer.readSliceAsInts(entriesCount)

            val entryVersions = buffer.readSliceAsInts(entriesCount)

            val entryChildrenCounts = buffer.readSliceAsShorts(entriesCount)

            val entryChildrenIds = Array(entriesCount) { IntArray(entryChildrenCounts[it].toInt()) }
            for (i in 0 until entriesCount) {
                accumulator = 0
                for (j in 0 until entryChildrenCounts[i]) {
                    val delta = buffer.readUnsignedShort()
                    accumulator += delta
                    entryChildrenIds[i][j] = accumulator
                }
            }

            val entryChildrenIdentifiers: Array<IntBuffer>? = if (flags and Flag.IDENTIFIERS.id != 0) {
                Array(entriesCount) { buffer.readSliceAsInts(entryChildrenCounts[it].toInt()) }
            } else null

            val entries = arrayOfNulls<ArchiveInfo?>(entryIds[entryIds.size - 1] + 1)
            for (i in 0 until entriesCount) {
                val children = (0 until entryChildrenCounts[i]).map {
                    ArchiveInfo.FileInfo(entryChildrenIds[i][it], entryChildrenIdentifiers?.get(i)?.get(it))
                }
                entries[entryIds[i]] = (ArchiveInfo(entryIds[i], entryIdentifiers?.get(i), entryCrcs[i], entryVersions[i], children))
            }
            return IndexReference(format, version, flags, entries.asList())
        }
    }

    override fun toString(): String {
        return "IndexReference(format=$format, version=$version, flags=$flags, archives=${archives.size})"
    }

    data class ArchiveInfo(
            val id: Int,
            val identifier: Int?,
            val crc: Int,
            val version: Int,
            val files: List<FileInfo>
    ) {
        override fun toString(): String {
            return "ArchiveInfo(id=$id, identifier=$identifier, files=${files.size})"
        }

        data class FileInfo(val id: Int, var identifier: Int?)
    }

    internal enum class Flag(idPosition: Int) {
        IDENTIFIERS(0);

        val id = 1 shl idPosition
    }
}