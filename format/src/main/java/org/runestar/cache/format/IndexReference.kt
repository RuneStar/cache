package org.runestar.cache.format

import java.nio.IntBuffer

class IndexReference(val volume: Volume) {

    val format: Int

    val version: Int

    val hasNames: Boolean

    val archives: List<ArchiveInfo?>

    init {
        val buffer = volume.decompressed
        format = buffer.readUnsignedByte().toInt()
        check(format in 5..6)
        version = if (format >= 6) buffer.readInt() else 0
        hasNames = buffer.readUnsignedByte().toInt() != 0
        val archivesCount = buffer.readUnsignedShort()
        val archiveIds = IntArray(archivesCount)
        var accumulator = 0
        for (a in 0 until archivesCount) {
            accumulator += buffer.readUnsignedShort()
            archiveIds[a] = accumulator
        }

        check(archiveIds.max() == archiveIds.lastOrNull())
        val maxArchiveId = archiveIds.last()

        val archiveNameHashes: IntBuffer? = if (hasNames) {
            buffer.readNioIntBuffer(archivesCount)
        } else {
            null
        }

        val archiveCrcs = buffer.readNioIntBuffer(archivesCount)

        val archiveVersions = buffer.readNioIntBuffer(archivesCount)

        val recordCounts = buffer.readNioShortBuffer(archivesCount)

        val recordIds = Array(archivesCount) { IntArray(recordCounts[it].toUnsignedInt()) }
        for (a in 0 until archivesCount) {
            accumulator = 0
            for (r in 0 until recordCounts[a].toUnsignedInt()) {
                accumulator += buffer.readUnsignedShort()
                recordIds[a][r] = accumulator
            }

            check(recordIds[a].max() == recordIds[a].lastOrNull())
        }

        val recordNameHashes: Array<IntBuffer>? = if (hasNames) {
            Array(archivesCount) { buffer.readNioIntBuffer(recordCounts[it].toUnsignedInt()) }
        } else {
            null
        }

        val archives = arrayOfNulls<ArchiveInfo?>(maxArchiveId + 1)
        for (a in 0 until archivesCount) {
            val maxRecordId = recordIds[a].last()
            val records = arrayOfNulls<ArchiveInfo.RecordInfo?>(maxRecordId + 1)
            for (r in 0 until recordCounts[a]) {
                records[recordIds[a][r]] = ArchiveInfo.RecordInfo(recordIds[a][r], recordNameHashes?.get(a)?.get(r))
            }
            archives[archiveIds[a]] = ArchiveInfo(archiveIds[a], archiveNameHashes?.get(a), archiveCrcs[a], archiveVersions[a], records.asList())
        }
        this.archives = archives.asList()
    }

    override fun toString(): String {
        return "IndexReference(archives=$archives)"
    }

    data class ArchiveInfo(
            val id: Int,
            val nameHash: Int?,
            val crc: Int,
            val version: Int,
            val records: List<RecordInfo?>
    ) {
        override fun toString(): String {
            return "ArchiveInfo(id=$id, nameHash=$nameHash, version=$version, records=${records.size})"
        }

        data class RecordInfo(val id: Int, val nameHash: Int?)
    }
}