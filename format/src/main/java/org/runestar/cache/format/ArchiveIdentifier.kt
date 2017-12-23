package org.runestar.cache.format

data class ArchiveIdentifier(
        val id: Int,
        val nameHash: Int?,
        val name: String?
)