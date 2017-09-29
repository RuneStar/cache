package com.runesuite.cache.format.net

import com.runesuite.cache.format.CacheReference
import com.runesuite.cache.format.IndexReference
import com.runesuite.cache.format.ReadableStore
import com.runesuite.cache.format.Volume
import com.runesuite.general.suggestedHost
import io.netty.channel.nio.NioEventLoopGroup
import java.nio.channels.ClosedChannelException
import java.util.concurrent.CompletableFuture
import com.runesuite.general.PORT as GENERAL_PORT
import com.runesuite.general.revision as generalRevision

class NetStore
private constructor(
        val host: String,
        val port: Int,
        val revision: Int
) : java.nio.channels.Channel, ReadableStore {

    private val group = NioEventLoopGroup(1)

    private var connection: NetStoreConnection? = null

    private fun getVolume0(index: Int, archive: Int): CompletableFuture<Volume> {
        if (!isOpen) throw ClosedChannelException()
        val conn = connection
        if (conn == null || !conn.isOpen) {
            connection = NetStoreConnection(group, host, port, revision)
        }
        return checkNotNull(connection).getVolume0(index, archive)
    }

    override fun getIndexReference(index: Int): CompletableFuture<IndexReference> {
        return getVolume0(255, index).thenApply { IndexReference(it) }
    }

    override fun getReference(): CompletableFuture<CacheReference> {
        return getVolume0(255, 255).thenApply { CacheReference.read(it.decompressed) }
    }

    override fun getVolume(index: Int, archive: Int): CompletableFuture<Volume> {
        return getVolume0(index, archive)
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
        fun open(host: String = "oldschool1.runescape.com", port: Int = GENERAL_PORT, revision: Int = generalRevision): NetStore {
            return NetStore(host, port, revision)
        }
    }
}