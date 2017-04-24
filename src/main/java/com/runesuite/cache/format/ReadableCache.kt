package com.runesuite.cache.format

import com.runesuite.cache.format.ArchiveId
import com.runesuite.cache.format.ChecksumTable
import com.runesuite.cache.format.CompressedFile
import java.io.Closeable

interface ReadableCache : Closeable {

    val indexCount: Int get() {
        return getChecksumTable().entries.size
    }

    fun getChecksumTable(): ChecksumTable {
        return createChecksumTable()
    }

    fun createChecksumTable(): ChecksumTable {
        val checksumTableEntries = (0 until indexCount).map {
            val compressed = getReferenceTableCompressed(it)
            val ref = ReferenceTable.read(compressed.data)
            ChecksumTable.Entry(compressed.crc, ref.version)
        }
        return ChecksumTable(checksumTableEntries)
    }

    fun getReferenceTableCompressed(index: Int): CompressedFile

    fun getReferenceTable(index: Int): ReferenceTable {
        return ReferenceTable.read(getReferenceTableCompressed(index).data)
    }

    fun getArchiveCompressed(archiveId: ArchiveId): CompressedFile?
}