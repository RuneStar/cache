package org.runestar.cache.format.net

import io.netty.channel.nio.NioEventLoopGroup
import org.runestar.cache.format.IndexReference
import org.runestar.cache.format.ReadableCache
import org.runestar.cache.format.StoreReference
import org.runestar.cache.format.Volume
import java.nio.channels.Channel
import java.nio.channels.ClosedChannelException
import java.util.concurrent.CompletableFuture

class NetCache
private constructor(
        val host: String,
        val revision: Int,
        val port: Int
) : Channel, ReadableCache {

    private val group = NioEventLoopGroup(1)

    private var connection: NetStoreConnection? = null

    private fun requestFile(index: Int, volume: Int): CompletableFuture<Volume> {
        if (!isOpen) throw ClosedChannelException()
        val conn = connection
        if (conn == null || !conn.isOpen) {
            connection = NetStoreConnection(group, host, port, revision)
        }
        return checkNotNull(connection).requestFile(index, volume)
    }

    override fun getIndexReference(index: Int): CompletableFuture<IndexReference> {
        return requestFile(255, index).thenApply { IndexReference(it) }
    }

    override fun getReference(): CompletableFuture<StoreReference> {
        return requestFile(255, 255).thenApply { StoreReference.read(it.decompress()) }
    }

    override fun getVolume(index: Int, archive: Int): CompletableFuture<Volume> {
        return requestFile(index, archive)
    }

    override fun close() {
        connection?.close()
        group.shutdownGracefully()
    }

    override fun isOpen(): Boolean {
        return !group.isShuttingDown
    }

    companion object {

        fun open(
                host: String,
                revision: Int,
                port: Int = 43594
        ): NetCache {
            return NetCache(host, revision, port)
        }
    }
}