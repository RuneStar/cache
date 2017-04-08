package com.runesuite.cache.fs

import com.runesuite.cache.ReferenceTable
import java.io.Closeable

class Cache(val store: Store) : AutoCloseable, Closeable {

    val referenceTables = store.indexBuffers.indices.map { ReferenceTable.read(store.getReference(it).decompress()) }

    override fun close() {
        store.close()
    }
}