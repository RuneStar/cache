package com.runesuite.cache.net

import com.runesuite.cache.buffer.asList
import io.netty.buffer.Unpooled
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import mu.KotlinLogging
import java.io.Closeable
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future

class CacheClient
@Throws(IOException::class)
constructor(val revision: Int, val host: String, val port: Int) : AutoCloseable, Closeable {

    private val logger = KotlinLogging.logger {  }

    private val vertx = Vertx.vertx()
    private val socket: NetSocket

    private val responses: MutableMap<Pair<Int, Int>, CompletableFuture<FileResponse>> = ConcurrentHashMap()
    private val responseBuffer = Unpooled.buffer(FileResponse.SIZE)

    init {
        val netClient = vertx.createNetClient()
        val socketFuture = CompletableFuture<NetSocket>()
        netClient.connect(port, host) {
            when (it.succeeded()) {
                true -> socketFuture.complete(it.result())
                false -> { close(); socketFuture.completeExceptionally(it.cause()) }
            }
        }
        socket = socketFuture.get()
        val handshakeRequest = HandshakeRequest(revision)
        logger.debug { handshakeRequest }
        write(handshakeRequest)
        val handshakeResponseFuture = CompletableFuture<HandshakeResponse>()
        socket.handler { handshakeResponseFuture.complete(HandshakeResponse(it.byteBuf)) }
        val handshakeResponse = handshakeResponseFuture.get()
        logger.debug { handshakeResponse }
        if (handshakeResponse.status != HandshakeResponse.Status.SUCCESS) {
            close()
            throw IOException(handshakeResponse.toString())
        }
        val connectionInfo = ConnectionInfo(ConnectionInfo.State.LOGGED_OUT)
        logger.debug { connectionInfo }
        write(connectionInfo)
        socket.handler { buffer ->
            val byteBuf = buffer.byteBuf
            logger.debug { "Response: ${byteBuf.asList()}" }
            responseBuffer.writeBytes(byteBuf)
            val response = FileResponse(responseBuffer)
            logger.debug { response }
            if (!response.done) {
                return@handler
            }
            logger.debug { "compressedData=${response.compressedData.asList()}" }
            responses.remove(response.index to response.file)!!.complete(response)
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

    override fun close() {
        vertx.close()
    }
}