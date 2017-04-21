package com.runesuite.cache.net

import com.runesuite.cache.ArchiveId
import com.runesuite.cache.ChecksumTable
import com.runesuite.cache.CompressedFile
import com.runesuite.cache.ReadableCache
import com.runesuite.cache.extensions.readableArray
import io.netty.buffer.CompositeByteBuf
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

class NetClientCache
@Throws(IOException::class)
constructor(
        val revision: Int,
        val host: String,
        val port: Int
) : AutoCloseable, Closeable, ReadableCache {

    private val logger = KotlinLogging.logger {  }

    private val vertx = Vertx.vertx()

    private val socket: NetSocket

    private val responses: MutableMap<ArchiveId, CompletableFuture<FileResponse>> = ConcurrentHashMap()

    private val requestBuffer = Unpooled.buffer(5)

    private var responseBuffer: CompositeByteBuf = Unpooled.compositeBuffer()

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
        logger.debug { "Response: ${byteBuf.readableBytes()}, ${byteBuf.readableArray().contentToString()}" }
        Chunker.Default.join(responseBuffer, byteBuf)
        if (responseBuffer.readableBytes() < FileResponse.HEADER_LENGTH + CompressedFile.HEADER_LENGTH) {
            logger.debug { "Not enough data to read headers" }
            return
        }
        val response = FileResponse(responseBuffer)
        check(responses.contains(response.archiveId)) { "Unrequested response: ${response.archiveId}" }
        if (!response.compressedFile.done) {
            return
        }
        logger.debug { "Done: $response" }
        val responseFuture = responses.remove(response.archiveId)!!
        responseFuture.complete(response)
        responseBuffer = Unpooled.compositeBuffer()
    }

    private fun write(request: Request) {
        requestBuffer.clear()
        request.write(requestBuffer)
        logger.debug { "Writing: ${requestBuffer.readableArray().contentToString()}" }
        socket.write(Buffer.buffer(requestBuffer))
    }

    fun request(archiveId: ArchiveId): Future<FileResponse> {
        val responseFuture = CompletableFuture<FileResponse>()
        val fileRequest = FileRequest(archiveId)
        logger.debug { fileRequest }
        responses[archiveId] = responseFuture
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
        logger.debug { "Response: ${handshakeResponse.input.readableArray().contentToString()}" }
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

    override fun getArchiveCompressed(archiveId: ArchiveId): CompressedFile {
        return request(archiveId).get().compressedFile
    }

    override fun getReferenceTableCompressed(index: Int): CompressedFile {
        return getArchiveCompressed(ArchiveId(REFERENCE_INDEX, index))
    }

    override fun getChecksumTable(): ChecksumTable {
        return ChecksumTable.read(getArchiveCompressed(CHECKSUM_ARCHIVE).data)
    }

    override fun close() {
        vertx.close()
    }

    private companion object {
        const val REFERENCE_INDEX = 255
        val CHECKSUM_ARCHIVE = ArchiveId(REFERENCE_INDEX, 255)
    }
}