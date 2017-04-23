package com.runesuite.cache.net

import com.runesuite.cache.ArchiveId
import com.runesuite.cache.ChecksumTable
import com.runesuite.cache.CompressedFile
import com.runesuite.cache.ReadableCache
import com.runesuite.cache.extensions.readableArray
import com.runesuite.general.RuneScape
import io.netty.buffer.CompositeByteBuf
import io.netty.buffer.PooledByteBufAllocator
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import mu.KotlinLogging
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future

open class NetClientCache
@Throws(IOException::class)
constructor(
        revisionMinimum: Int,
        val host: String,
        val port: Int
) : ReadableCache {

    class Default : NetClientCache(RuneScape.revisionMinimum, "oldschool29.runescape.com", 43594)

    private val logger = KotlinLogging.logger {  }

    var revision: Int
        private set

    private val vertx = Vertx.vertx()

    private val netClient = vertx.createNetClient()

    private var socket: NetSocket

    private val responses: MutableMap<ArchiveId, CompletableFuture<FileResponse>> = ConcurrentHashMap()

    private val requestBuffer = PooledByteBufAllocator.DEFAULT.buffer(5)

    private var responseBuffer: CompositeByteBuf = PooledByteBufAllocator.DEFAULT.compositeBuffer()

    init {
        revision = revisionMinimum - 1
        var responseStatus: HandshakeResponse.Status
        do {
            revision++
            socket = createSocket()
            responseStatus = handshake(revision).status
        } while (responseStatus != HandshakeResponse.Status.SUCCESS)
        val connectionInfoOffer = ConnectionInfoOffer(ConnectionInfoOffer.State.LOGGED_OUT)
        logger.trace { connectionInfoOffer }
        write(connectionInfoOffer)
        socket.handler { onSocketRead(it) }
    }

    private fun onSocketRead(input: Buffer) {
        val byteBuf = input.byteBuf
        logger.trace { "Response: ${byteBuf.readableBytes()}, ${byteBuf.readableArray().contentToString()}" }
        Chunker.Default.join(responseBuffer, byteBuf)
        if (responseBuffer.readableBytes() < FileResponse.HEADER_LENGTH + CompressedFile.HEADER_LENGTH) {
            logger.trace { "Not enough data to read headers" }
            return
        }
        val response = FileResponse(responseBuffer)
        check(responses.contains(response.archiveId)) { "Unrequested response: ${response.archiveId}" }
        if (!response.compressedFile.done) {
            return
        }
        logger.trace { "Done: $response" }
        val responseFuture = responses.remove(response.archiveId)!!
        responseFuture.complete(response)
        responseBuffer = PooledByteBufAllocator.DEFAULT.compositeBuffer()
    }

    private fun write(request: Request) {
        requestBuffer.clear()
        request.write(requestBuffer)
        logger.trace { "Writing: ${requestBuffer.readableArray().contentToString()}" }
        socket.write(Buffer.buffer(requestBuffer))
    }

    fun request(archiveId: ArchiveId): Future<FileResponse> {
        val responseFuture = CompletableFuture<FileResponse>()
        val fileRequest = FileRequest(archiveId)
        logger.trace { fileRequest }
        responses[archiveId] = responseFuture
        write(fileRequest)
        return responseFuture
    }

    private fun handshake(revision: Int): HandshakeResponse {
        val handshakeRequest = HandshakeRequest(revision)
        logger.trace { handshakeRequest }
        val handshakeResponseFuture = CompletableFuture<HandshakeResponse>()
        socket.handler { handshakeResponseFuture.complete(HandshakeResponse(it.byteBuf)) }
        write(handshakeRequest)
        val handshakeResponse = handshakeResponseFuture.get()
        logger.trace { "Response: ${handshakeResponse.input.readableArray().contentToString()}" }
        logger.trace { handshakeResponse }
        return handshakeResponse
    }

    private fun createSocket(): NetSocket {
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

    final override fun close() {
        vertx.close()
        requestBuffer.release()
    }

    private companion object {
        const val REFERENCE_INDEX = 255
        val CHECKSUM_ARCHIVE = ArchiveId(REFERENCE_INDEX, 255)
    }
}