package com.runesuite.cache.fs

import com.runesuite.cache.ChecksumTable
import com.runesuite.cache.Crc32
import com.runesuite.cache.ReferenceTable
import java.io.Closeable

class Cache(val store: Store) : AutoCloseable, Closeable {

    val referenceTables: List<ReferenceTable>

    val checksumTable: ChecksumTable

    init {
        val checksumTableEntries = ArrayList<ChecksumTable.Entry>(store.indexBuffers.size)
        referenceTables = store.indexBuffers.indices.map {
            val buf = store.getReference(it).decompress()
            val bufCrc = Crc32.checksum(buf)
            val ref = ReferenceTable.read(buf)
            val refVersion = ref.version
            checksumTableEntries.add(ChecksumTable.Entry(bufCrc, refVersion))
            ref
        }
        checksumTable = ChecksumTable(checksumTableEntries)
    }

    override fun close() {
        store.close()
    }
}