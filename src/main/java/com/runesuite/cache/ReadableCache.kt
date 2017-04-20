package com.runesuite.cache

interface ReadableCache {

    fun getChecksumTable(): ChecksumTable

    fun getReferenceTable(index: Int): CompressedFile

    fun getArchive(archiveId: ArchiveId): CompressedFile
}