package org.runestar.cache.format

import java.nio.IntBuffer

class IndexReference(val volume: Volume) {

    val format: Int

    val version: Int

    val hasNames: Boolean

    val archives: List<ArchiveInfo?>

    val archiveIds: IntArray

    init {
        val buffer = volume.decompress()
        format = buffer.readUnsignedByte().toInt()
        check(format in 5..6)
        version = if (format >= 6) buffer.readInt() else 0
        hasNames = buffer.readUnsignedByte().toInt() != 0
        val archiveCount = buffer.readUnsignedShort()
        archiveIds = IntArray(archiveCount)
        var accumulator = 0
        for (a in 0 until archiveCount) {
            accumulator += buffer.readUnsignedShort()
            archiveIds[a] = accumulator
        }

        check(archiveIds.max() == archiveIds.lastOrNull())
        val maxArchiveId = archiveIds.last()

        val archiveNameHashes: IntBuffer? = if (hasNames) {
            buffer.readNioIntBuffer(archiveCount)
        } else {
            null
        }

        val archiveCrcs = buffer.readNioIntBuffer(archiveCount)

        val archiveVersions = buffer.readNioIntBuffer(archiveCount)

        val recordCounts = buffer.readNioShortBuffer(archiveCount)

        val recordIds = Array(archiveCount) { IntArray(recordCounts[it].toUnsignedInt()) }
        for (a in 0 until archiveCount) {
            accumulator = 0
            for (r in 0 until recordCounts[a].toUnsignedInt()) {
                accumulator += buffer.readUnsignedShort()
                recordIds[a][r] = accumulator
            }

            check(recordIds[a].max() == recordIds[a].lastOrNull())
        }

        val recordNameHashes: Array<IntBuffer>? = if (hasNames) {
            Array(archiveCount) { buffer.readNioIntBuffer(recordCounts[it].toUnsignedInt()) }
        } else {
            null
        }

        val archives = arrayOfNulls<ArchiveInfo?>(maxArchiveId + 1)
        for (a in 0 until archiveCount) {
            val maxRecordId = recordIds[a].last()
            val records = arrayOfNulls<RecordInfo?>(maxRecordId + 1)
            for (r in 0 until recordCounts[a].toUnsignedInt()) {
                records[recordIds[a][r]] = RecordInfo(recordNameHashes?.get(a)?.get(r))
            }
            archives[archiveIds[a]] = ArchiveInfo(archiveNameHashes?.get(a), archiveCrcs[a], archiveVersions[a], records.asList(), recordIds[a])
        }
        this.archives = archives.asList()
    }

    fun getArchiveId(name: String): Int? {
        val nameHash = name.hashCode()
        val id = archives.indexOfFirst { it != null && it.nameHash == nameHash }
        if (id == -1) return null
        return id
    }

    override fun toString(): String {
        return "IndexReference(archives=$archives)"
    }

    class ArchiveInfo(
            val nameHash: Int?,
            val crc: Int,
            val version: Int,
            val records: List<RecordInfo?>,
            val recordIds: IntArray
    ) {
        override fun toString(): String {
            return "ArchiveInfo(nameHash=$nameHash, version=$version, records=${records.size})"
        }
    }

    data class RecordInfo(val nameHash: Int?)
}