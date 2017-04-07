
package com.runesuite.cache

import com.runesuite.cache.extensions.readSmartInt
import io.netty.buffer.ByteBuf
import java.util.*

data class ReferenceTable(
        val format: Int,
        val version: Int?,
        val flags: Int,
        val entries: List<Entry>
) {

    companion object {

        fun read(buffer: ByteBuf): ReferenceTable {
            val format = buffer.readUnsignedByte().toInt()
            check(format in 5..7)
            val version = if (format >= 6) buffer.readInt() else null
            val flags = buffer.readUnsignedByte().toInt()
            val entriesCount = if (format >= 7) buffer.readSmartInt() else buffer.readUnsignedShort()
            val entryIds = IntArray(entriesCount)
            var accumlator = 0
            var maxEntryId = -1
            for (i in 0 until entriesCount) {
                val delta = if (format >= 7) buffer.readSmartInt() else buffer.readUnsignedShort()
                accumlator += delta
                entryIds[i] = accumlator
                maxEntryId = Math.max(maxEntryId, accumlator)
            }

            val entryIdentifiers: IntArray? = if (flags and Flag.IDENTIFIERS.id != 0) {
                IntArray(entriesCount).apply {
                    for (i in indices) {
                        set(i, buffer.readInt())
                    }
                }
            } else {
                null
            }

            val entryCrcs = IntArray(entriesCount).apply {
                for (i in indices) {
                    set(i, buffer.readInt())
                }
            }

            val entryHashes: IntArray? = if (flags and Flag.HASH.id != 0) {
                IntArray(entriesCount).apply {
                    for (i in indices) {
                        set(i, buffer.readInt())
                    }
                }
            } else {
                null
            }

            val entryWhirlpools: Array<ByteArray>? = if (flags and Flag.HASH.id != 0) {
                Array(entriesCount, { ByteArray(Whirlpool.HASH_LENGTH) }).apply {
                    for (i in indices) {
                        buffer.readBytes(get(i))
                    }
                }
            } else {
                null
            }

            var entryCompressedSizes: IntArray? = null
            var entryDecompressedSizes: IntArray? = null
            if (flags and Flag.SIZES.id != 0) {
                entryCompressedSizes = IntArray(entriesCount)
                entryDecompressedSizes = IntArray(entriesCount)
                for (i in 0 until entriesCount) {
                    entryCompressedSizes[i] = buffer.readInt()
                    entryDecompressedSizes[i] = buffer.readInt()
                }
            }

            val entryVersions = IntArray(entriesCount).apply {
                for (i in indices) {
                    set(i, buffer.readInt())
                }
            }

            val entryChildrenCounts = IntArray(entriesCount).apply {
                for (i in indices) {
                    set(i, if (format >= 7) buffer.readSmartInt() else buffer.readUnsignedShort())
                }
            }

            val entryChildrenIds = Array(entriesCount) { IntArray(entryChildrenCounts[it]) }
            for (i in 0 until entriesCount) {
                accumlator = 0
                for (j in 0 until entryChildrenCounts[i]) {
                    val delta = if (format >= 7) buffer.readSmartInt() else buffer.readUnsignedShort()
                    accumlator += delta
                    entryChildrenIds[i][j] = accumlator
                }
            }

            var entryChildrenIdentifiers: Array<IntArray>? = null
            if (flags and Flag.IDENTIFIERS.id != 0) {
                entryChildrenIdentifiers = Array<IntArray>(entriesCount) { IntArray(entryChildrenCounts[it]) }
                for (i in 0 until entriesCount) {
                    for (j in 0 until entryChildrenCounts[i]) {
                        entryChildrenIdentifiers[i][j] = buffer.readInt()
                    }
                }
            }

            val entries = ArrayList<Entry>(entriesCount)
            for (i in 0 until entriesCount) {
                val children = ArrayList<Entry.Child>(entryChildrenCounts[i])
                for (j in children.indices) {
                    children.add(Entry.Child(entryChildrenIds[i][j], entryChildrenIdentifiers?.get(i)?.get(j)))
                }
                entries.add(Entry(entryIds[i], entryIdentifiers?.get(i), entryHashes?.get(i), entryCrcs[i],
                        entryWhirlpools?.get(i), entryCompressedSizes?.get(i), entryDecompressedSizes?.get(i),
                        entryVersions[i], children))
            }

            return ReferenceTable(format, version, flags, entries)
        }
    }

    class Entry(
            val id: Int,
            val identifier: Int?,
            val hash: Int?,
            val crc: Int,
            val whirlpool: ByteArray?,
            val compressedSize: Int?,
            val decompressedSize: Int?,
            val version: Int,
            val children: List<Child>
    ) {

        init {
            check((compressedSize == null && decompressedSize == null) ||
                    (compressedSize != null && decompressedSize != null))
        }

        override fun toString(): String {
            return "Entry(id=$id, identifier=$identifier, hash=$hash, crc=$crc, whirlpool=${Arrays.toString(whirlpool)}, compressedSize=$compressedSize, decompressedSize=$decompressedSize, version=$version, children=$children)"
        }

        data class Child(val id: Int, var identifier: Int? = null)
    }

    enum class Flag(idPosition: Int) {
        IDENTIFIERS(0),
        WHIRLPOOL(1),
        SIZES(2),
        HASH(3);

        val id = 1 shl idPosition
    }
}