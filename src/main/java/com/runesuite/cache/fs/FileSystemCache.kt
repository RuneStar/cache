package com.runesuite.cache.fs

import com.runesuite.cache.*
import java.io.Closeable
import java.nio.file.Path

class FileSystemCache(val folder: Path) : AutoCloseable, Closeable, ReadableCache {

    val store = Store(folder)

    val referenceTables: List<ReferenceTable>

    private val checksumTable2: ChecksumTable

    init {
        val checksumTableEntries = ArrayList<ChecksumTable.Entry>(store.indexBuffers.size)
        referenceTables = store.indexBuffers.indices.map {
            val compressed = store.getReferenceTable(it)
            val crc = compressed.crc
            val ref = ReferenceTable.read(compressed.decompress())
            val refVersion = ref.version
            checksumTableEntries.add(ChecksumTable.Entry(crc, refVersion))
            ref
        }
        checksumTable2 = ChecksumTable(checksumTableEntries)
    }

    override fun getArchive(archiveId: ArchiveId): CompressedFile {
        return store.getArchive(archiveId)
    }

    override fun getChecksumTable(): ChecksumTable {
        return checksumTable2
    }

    override fun getReferenceTable(index: Int): CompressedFile {
        return store.getReferenceTable(index)
    }

    override fun close() {
        store.close()
    }
}