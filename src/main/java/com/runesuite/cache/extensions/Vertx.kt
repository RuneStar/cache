package com.runesuite.cache.extensions

import io.vertx.core.net.NetClient
import io.vertx.core.net.NetSocket
import java.io.IOException
import java.util.concurrent.CompletableFuture

@Throws(IOException::class)
fun NetClient.connectBlocking(port: Int, host: String): NetSocket {
    val socketFuture = CompletableFuture<NetSocket>()
    connect(port, host) {
        when (it.succeeded()) {
            true -> socketFuture.complete(it.result())
            false -> socketFuture.completeExceptionally(it.cause())
        }
    }
    try {
        return socketFuture.get()
    } catch (e: Exception) {
        throw IOException(e.cause)
    }
}