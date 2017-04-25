package com.runesuite.cache.format

import com.runesuite.cache.extensions.readSliceAsInts
import com.runesuite.cache.extensions.readSliceAsShorts
import io.netty.buffer.ByteBuf
import java.nio.IntBuffer

data class ReferenceTable(
        val format: Int,
        val version: Int,
        val flags: Int,
        val entries: List<Entry?>
) {

    companion object {

        fun read(buffer: ByteBuf): ReferenceTable {
            val format = buffer.readUnsignedByte().toInt()
            check(format in 5..6)
            val version = if (format >= 6) buffer.readInt() else 0
            val flags = buffer.readUnsignedByte().toInt()
            val entriesCount = buffer.readUnsignedShort()
            val entryIds = IntArray(entriesCount)
            var accumlator = 0
            for (i in 0 until entriesCount) {
                val delta = buffer.readUnsignedShort()
                accumlator += delta
                entryIds[i] = accumlator
            }

            val entryIdentifiers: IntBuffer? = if (flags and Flag.IDENTIFIERS.id != 0) {
                buffer.readSliceAsInts(entriesCount)
            } else null

            val entryCrcs = buffer.readSliceAsInts(entriesCount)

            val entryVersions = buffer.readSliceAsInts(entriesCount)

            val entryChildrenCounts = buffer.readSliceAsShorts(entriesCount)

            val entryChildrenIds = Array(entriesCount) { IntArray(entryChildrenCounts[it].toInt()) }
            for (i in 0 until entriesCount) {
                accumlator = 0
                for (j in 0 until entryChildrenCounts[i]) {
                    val delta = buffer.readUnsignedShort()
                    accumlator += delta
                    entryChildrenIds[i][j] = accumlator
                }
            }

            val entryChildrenIdentifiers: Array<IntBuffer>? = if (flags and Flag.IDENTIFIERS.id != 0) {
                Array(entriesCount) { buffer.readSliceAsInts(entryChildrenCounts[it].toInt()) }
            } else null

            val entries = arrayOfNulls<Entry?>(entryIds[entryIds.size - 1] + 1)
            for (i in 0 until entriesCount) {
                val children = (0 until entryChildrenCounts[i]).map {
                    Entry.Child(entryChildrenIds[i][it], entryChildrenIdentifiers?.get(i)?.get(it))
                }
                entries[entryIds[i]] = (Entry(entryIds[i], entryIdentifiers?.get(i), entryCrcs[i], entryVersions[i], children))
            }
            return ReferenceTable(format, version, flags, entries.asList())
        }
    }

    override fun toString(): String {
        return "ReferenceTable(format=$format, version=$version, flags=$flags, entries=${entries.size})"
    }

    data class Entry(
            val id: Int,
            val identifier: Int?,
            val crc: Int,
            val version: Int,
            val children: List<Child>
    ) {
        override fun toString(): String {
            return "Entry(id=$id, identifier=$identifier, children=${children.size})"
        }

        data class Child(val id: Int, var identifier: Int?)
    }

    internal enum class Flag(idPosition: Int) {
        IDENTIFIERS(0);

        val id = 1 shl idPosition
    }
}