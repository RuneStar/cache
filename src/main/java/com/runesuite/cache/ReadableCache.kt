package com.runesuite.cache

import java.util.*

interface ReadableCache {

    val indexCount: Int get() {
        return getChecksumTable().entries.size
    }

    fun getChecksumTable(): ChecksumTable {
        return createChecksumTable()
    }

    fun createChecksumTable(): ChecksumTable {
        val checksumTableEntries = ArrayList<ChecksumTable.Entry>(indexCount)
        (0 until indexCount).forEach {
            val compressed = getReferenceTableCompressed(it)
            val ref = ReferenceTable.read(compressed.data)
            checksumTableEntries.add(ChecksumTable.Entry(compressed.crc, ref.version))
        }
        return ChecksumTable(checksumTableEntries)
    }

    fun getReferenceTableCompressed(index: Int): CompressedFile

    fun getReferenceTable(index: Int): ReferenceTable {
        return ReferenceTable.read(getReferenceTableCompressed(index).data)
    }

    fun getArchiveCompressed(archiveId: ArchiveId): CompressedFile
}