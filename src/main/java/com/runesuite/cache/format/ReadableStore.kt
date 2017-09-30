package com.runesuite.cache.format

import java.util.concurrent.CompletableFuture

interface ReadableStore {

    fun getReference(): CompletableFuture<StoreReference>

    fun getIndexReference(index: Int): CompletableFuture<IndexReference>

    fun getVolume(index: Int, volume: Int): CompletableFuture<out Volume?>
}