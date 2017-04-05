package com.runesuite.cache.net

import com.runesuite.cache.extensions.readSliceMax
import com.runesuite.cache.extensions.readableToString
import io.netty.buffer.ByteBuf
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

    private val responses: MutableMap<FileId, CompletableFuture<FileResponse>> = ConcurrentHashMap()

    private val requestBuffer = Unpooled.buffer(5)

    private val responseBuffers = ArrayList<ByteBuf>()

    init {
        socket = createSocket()
        val handshakeResponse = handshake(revision)
        if (handshakeResponse.status != HandshakeResponse.Status.SUCCESS) {
            close()
            throw IOException(handshakeResponse.toString())
        }
        val connectionInfoOffer = ConnectionInfoOffer(ConnectionInfoOffer.State.LOGGED_OUT)
        logger.debug { connectionInfoOffer }
        write(connectionInfoOffer)
        socket.handler(this::onSocketRead)
    }

    private fun onSocketRead(input: Buffer) {
        val byteBuf = input.byteBuf
        logger.debug { "Response: ${byteBuf.readableToString()}" }
        if (responseBuffers.isEmpty()) {
            responseBuffers.add(byteBuf.readSliceMax(FileResponse.CHUNK_LENGTH))
        }
        while (byteBuf.isReadable) {
            val chunk = byteBuf.readSliceMax(FileResponse.CHUNK_LENGTH)
            if (chunk.getByte(0).toInt() == -1) {
                chunk.skipBytes(1)
            }
            responseBuffers.add(chunk)
        }
        val responseBuffer = Unpooled.wrappedBuffer(*responseBuffers.toTypedArray())
        val response = FileResponse(responseBuffer)
        check(responses.contains(response.fileId)) { "Unrequested response: ${response.fileId}" }
        if (!response.compressedFile.done) {
            return
        }
        logger.debug { "Done: $response" }
        val responseFuture = responses.remove(response.fileId)!!
        responseFuture.complete(response)
        responseBuffers.clear()
    }

    private fun write(request: Request) {
        requestBuffer.clear()
        request.write(requestBuffer)
        logger.debug { "Writing: ${requestBuffer.readableToString()}" }
        socket.write(Buffer.buffer(requestBuffer))
    }

    fun request(fileId: FileId): Future<FileResponse> {
        val responseFuture = CompletableFuture<FileResponse>()
        val fileRequest = FileRequest(fileId)
        logger.debug { fileRequest }
        responses[fileId] = responseFuture
        write(fileRequest)
        return responseFuture
    }

    private fun handshake(revision: Int): HandshakeResponse {
        val handshakeRequest = HandshakeRequest(revision)
        logger.debug { handshakeRequest }
        val handshakeResponseFuture = CompletableFuture<HandshakeResponse>()
        socket.handler { handshakeResponseFuture.complete(HandshakeResponse(it.byteBuf)) }
        write(handshakeRequest)
        val handshakeResponse = handshakeResponseFuture.get()
        logger.debug { "Response: ${handshakeResponse.input.readableToString()}" }
        logger.debug { handshakeResponse }
        return handshakeResponse
    }

    private fun createSocket(): NetSocket {
        val netClient = vertx.createNetClient()
        val socketFuture = CompletableFuture<NetSocket>()
        netClient.connect(port, host) {
            when (it.succeeded()) {
                true -> socketFuture.complete(it.result())
                false -> { close(); socketFuture.completeExceptionally(it.cause()) }
            }
        }
        return socketFuture.get()
    }

    override fun close() {
        vertx.close()
    }
}