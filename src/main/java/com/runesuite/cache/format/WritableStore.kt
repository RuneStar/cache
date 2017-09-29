package com.runesuite.cache.format

interface WritableStore : ReadableStore {

    fun setVolume(index: Int, archive: Int, volume: Volume)

    fun setReference(reference: CacheReference)

    fun setIndexReference(index: Int, indexReference: IndexReference)
}