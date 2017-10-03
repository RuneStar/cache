package com.runesuite.cache.format

import java.nio.channels.Channel
import java.util.concurrent.CompletableFuture

interface ReadableStore : Channel {

    fun getReference(): CompletableFuture<StoreReference>

    fun getIndexReference(index: Int): CompletableFuture<IndexReference>

    fun getVolume(index: Int, volume: Int): CompletableFuture<out Volume?>
}