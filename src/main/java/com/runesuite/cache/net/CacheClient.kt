package com.runesuite.cache.net

import com.runesuite.cache.buffer.asList
import io.netty.buffer.Unpooled
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import mu.KotlinLogging
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

class CacheClient(val revision: Int, val host: String, val port: Int) : AutoCloseable {

    private val logger = KotlinLogging.logger {  }

    private val vertx = Vertx.vertx()
    private val socket: NetSocket

    private val responses: MutableMap<Pair<Int, Int>, CompletableFuture<FileResponse>> = LinkedHashMap()
    private val responseBuffer = Unpooled.buffer(FileResponse.SIZE)

    init {
        val netClient = vertx.createNetClient()
        val socketFuture = CompletableFuture<NetSocket>()
        netClient.connect(port, host) {
            when (it.succeeded()) {
                true -> socketFuture.complete(it.result())
                false -> socketFuture.completeExceptionally(it.cause())
            }
        }
        socket = socketFuture.get()
        write(HandshakeRequest(revision))
        val handshakeResponseFuture = CompletableFuture<HandshakeResponse>()
        socket.handler { handshakeResponseFuture.complete(HandshakeResponse(it.byteBuf)) }
        val handshakeResponse = handshakeResponseFuture.get()
        logger.debug { "Handshake response: ${handshakeResponse.byteBuf.asList()}" }
        when (handshakeResponse.status) {
            HandshakeResponse.Status.SUCCESS -> logger.debug { "Handshake response success" }
            HandshakeResponse.Status.OUTDATED -> fail { "Handshake response outdated" }
            HandshakeResponse.Status.UNKNOWN -> fail { "Handshake response unknown" }
        }
        write(ConnectionInfo(ConnectionInfo.State.LOGGED_OUT))
        socket.handler { buffer ->
            val byteBuf = buffer.byteBuf
            logger.debug { "Response: ${byteBuf.asList()}" }
            responseBuffer.writeBytes(byteBuf)
            logger.debug { "Response buffer: ${responseBuffer.asList()}" }
            val response = FileResponse(responseBuffer)
            logger.debug { "Response done: ${response.done}" }
            if (!response.done) {
                return@handler
            }
            responses[response.index to response.file]?.complete(response)
            responseBuffer.clear()
        }
    }

    private fun write(request: Request) {
        socket.write(Buffer.buffer(request.byteBuf))
        logger.debug { "Writing: ${request.byteBuf.asList()}" }
    }

    fun request(index: Int, file: Int): Future<FileResponse> {
        val responseFuture = CompletableFuture<FileResponse>()
        val fileRequest = FileRequest(index, file)
        responses[index to file] = responseFuture
        write(fileRequest)
        return responseFuture
    }

    private fun fail(message: () -> String) {
        close()
        throw IOException(message())
    }

    override fun close() {
        val closeFuture = CompletableFuture<Void?>()
        vertx.close { closeFuture.complete(null) }
        closeFuture.get()
    }
}