package com.runesuite.cache.fs

import com.runesuite.cache.ChecksumTable
import com.runesuite.cache.ReferenceTable
import java.io.Closeable

class Cache(val store: Store) : AutoCloseable, Closeable {

    val referenceTables: List<ReferenceTable>

    val checksumTable: ChecksumTable

    init {
        val checksumTableEntries = ArrayList<ChecksumTable.Entry>(store.indexBuffers.size)
        referenceTables = store.indexBuffers.indices.map {
            val compressed = store.getReference(it)
            val crc = compressed.crc
            val ref = ReferenceTable.read(compressed.decompress())
            val refVersion = ref.version
            checksumTableEntries.add(ChecksumTable.Entry(crc, refVersion))
            ref
        }
        checksumTable = ChecksumTable(checksumTableEntries)
    }

    override fun close() {
        store.close()
    }
}