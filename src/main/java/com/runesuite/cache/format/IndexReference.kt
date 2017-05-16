package com.runesuite.cache.format

import com.runesuite.cache.extensions.readSliceAsInts
import com.runesuite.cache.extensions.readSliceAsShorts
import java.nio.IntBuffer

class IndexReference(val volume: Volume) {

    val format: Int

    val version: Int

    val flags: Int

    val archives: List<ArchiveInfo?>

    init {
        val buffer = volume.decompressed
        format = buffer.readUnsignedByte().toInt()
        check(format in 5..6)
        version = if (format >= 6) buffer.readInt() else 0
        flags = buffer.readUnsignedByte().toInt()
        val entriesCount = buffer.readUnsignedShort()
        val entryIds = IntArray(entriesCount)
        var accumulator = 0
        for (i in 0 until entriesCount) {
            val delta = buffer.readUnsignedShort()
            accumulator += delta
            entryIds[i] = accumulator
        }

        val entryNameHashes: IntBuffer? = if (flags and Flag.NAMES.id != 0) {
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

        val entryChildrenNameHashes: Array<IntBuffer>? = if (flags and Flag.NAMES.id != 0) {
            Array(entriesCount) { buffer.readSliceAsInts(entryChildrenCounts[it].toInt()) }
        } else null

        val entries = arrayOfNulls<ArchiveInfo?>(entryIds[entryIds.size - 1] + 1)
        for (i in 0 until entriesCount) {
            val children = (0 until entryChildrenCounts[i]).map {
                ArchiveInfo.FileInfo(entryChildrenIds[i][it], entryChildrenNameHashes?.get(i)?.get(it))
            }
            entries[entryIds[i]] = ArchiveInfo(entryIds[i], entryNameHashes?.get(i), entryCrcs[i], entryVersions[i], children)
        }
        archives = entries.asList()
    }

    override fun toString(): String {
        return "IndexReference(archives=$archives)"
    }

    data class ArchiveInfo(
            val id: Int,
            val nameHash: Int?,
            val crc: Int,
            val version: Int,
            val files: List<FileInfo>
    ) {
        override fun toString(): String {
            return "ArchiveInfo(id=$id, nameHash=$nameHash, version=$version, files=${files.size})"
        }

        data class FileInfo(val id: Int, var nameHash: Int?)
    }

    enum class Flag(idPosition: Int) {
        NAMES(0);

        val id = 1 shl idPosition
    }
}