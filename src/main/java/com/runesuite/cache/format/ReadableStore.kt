package com.runesuite.cache.format

import java.util.concurrent.CompletableFuture

interface ReadableStore {

    fun getVolume(index: Int, archive: Int): CompletableFuture<Volume>

    fun getReference(): CompletableFuture<CacheReference>

    fun getIndexReference(index: Int): CompletableFuture<IndexReference>
}