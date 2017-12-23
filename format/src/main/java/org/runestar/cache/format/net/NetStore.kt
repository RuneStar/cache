package org.runestar.cache.format.net

import org.runestar.cache.format.StoreReference
import org.runestar.cache.format.IndexReference
import org.runestar.cache.format.ReadableStore
import org.runestar.cache.format.Volume
import org.runestar.general.suggestedHost
import io.netty.channel.nio.NioEventLoopGroup
import java.nio.channels.Channel
import java.nio.channels.ClosedChannelException
import java.util.concurrent.CompletableFuture
import org.runestar.general.PORT as GENERAL_PORT
import org.runestar.general.revision as generalRevision

class NetStore
private constructor(
        val host: String,
        val port: Int,
        val revision: Int
) : Channel, ReadableStore {

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
        return requestFile(255, 255).thenApply { StoreReference.read(it.decompressed) }
    }

    override fun getVolume(index: Int, volume: Int): CompletableFuture<Volume> {
        return requestFile(index, volume)
    }

    override fun close() {
        connection?.close()
        group.shutdownGracefully()
    }

    override fun isOpen(): Boolean {
        return !group.isShuttingDown
    }

    companion object {

        private val defaultHost by lazy { suggestedHost() }

        @JvmStatic
        fun open(
                host: String = defaultHost,
                port: Int = GENERAL_PORT,
                revision: Int = generalRevision
        ): NetStore {
            return NetStore(host, port, revision)
        }
    }
}