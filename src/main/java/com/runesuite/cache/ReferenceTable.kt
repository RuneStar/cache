package com.runesuite.cache

import com.runesuite.cache.extensions.readSliceAsInts
import com.runesuite.cache.extensions.readSmartInt
import com.runesuite.cache.extensions.readableArray
import io.netty.buffer.ByteBuf
import java.nio.IntBuffer
import java.util.*

data class ReferenceTable(
        val format: Int,
        val version: Int,
        val flags: Int,
        val entries: List<Entry>
) {

    companion object {

        fun read(buffer: ByteBuf): ReferenceTable {
            val format = buffer.readUnsignedByte().toInt()
            check(format in 5..7)
            val version = if (format >= 6) buffer.readInt() else 0
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

            val entryIdentifiers: IntBuffer? = if (flags and Flag.IDENTIFIERS.id != 0) {
                buffer.readSliceAsInts(entriesCount)
            } else null

            val entryCrcs = buffer.readSliceAsInts(entriesCount)

            val entryHashes: IntBuffer? = if (flags and Flag.HASH.id != 0) {
                buffer.readSliceAsInts(entriesCount)
            } else null

            val entryWhirlpools: Array<ByteArray>? = if (flags and Flag.WHIRLPOOL.id != 0) {
                Array(entriesCount) { buffer.readSlice(Whirlpool.HASH_LENGTH).readableArray() }
            } else null

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

            val entryVersions = buffer.readSliceAsInts(entriesCount)

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

            val entryChildrenIdentifiers: Array<IntBuffer>? = if (flags and Flag.IDENTIFIERS.id != 0) {
                Array(entriesCount) { buffer.readSliceAsInts(entryChildrenCounts[it]) }
            } else null

            val entries = ArrayList<Entry>(entriesCount)
            for (i in 0 until entriesCount) {
                val children = (0 until entryChildrenCounts[i]).map {
                    Entry.Child(entryChildrenIds[i][it], entryChildrenIdentifiers?.get(i)?.get(it))
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
            require((compressedSize == null) == (decompressedSize == null))
        }

        override fun toString(): String {
            return "Entry(id=$id, crc=$crc, version=$version, children=${children.size})"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false
            other as Entry
            if (id != other.id) return false
            if (identifier != other.identifier) return false
            if (hash != other.hash) return false
            if (crc != other.crc) return false
            if (!Arrays.equals(whirlpool, other.whirlpool)) return false
            if (compressedSize != other.compressedSize) return false
            if (decompressedSize != other.decompressedSize) return false
            if (version != other.version) return false
            if (children != other.children) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id
            result = 31 * result + (identifier ?: 0)
            result = 31 * result + (hash ?: 0)
            result = 31 * result + crc
            result = 31 * result + (whirlpool?.let { Arrays.hashCode(it) } ?: 0)
            result = 31 * result + (compressedSize ?: 0)
            result = 31 * result + (decompressedSize ?: 0)
            result = 31 * result + version
            result = 31 * result + children.hashCode()
            return result
        }


        data class Child(val id: Int, var identifier: Int?)
    }

    enum class Flag(idPosition: Int) {
        IDENTIFIERS(0),
        WHIRLPOOL(1),
        SIZES(2),
        HASH(3);

        val id = 1 shl idPosition
    }
}