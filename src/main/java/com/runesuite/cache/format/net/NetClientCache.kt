package com.runesuite.cache.format.net

import com.runesuite.cache.extensions.closeQuietly
import com.runesuite.cache.extensions.readableArray
import com.runesuite.cache.format.ArchiveId
import com.runesuite.cache.format.CacheReference
import com.runesuite.cache.format.Archive
import com.runesuite.cache.format.ReadableCache
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
        var handshakeStatus: HandshakeResponse.Status
        do {
            revision++
            socket = createSocket()
            handshakeStatus = handshake(revision).status
        } while (handshakeStatus == HandshakeResponse.Status.INCORRECT_REVISION)
        if (handshakeStatus != HandshakeResponse.Status.SUCCESS) {
            closeQuietly()
            throw IOException("$handshakeStatus. Revision: $revision")
        }
        val connectionInfoOffer = ConnectionInfoOffer(ConnectionInfoOffer.State.LOGGED_OUT)
        logger.trace { connectionInfoOffer }
        write(connectionInfoOffer)
        socket.handler { onSocketRead(it) }
    }

    private fun onSocketRead(input: Buffer) {
        val byteBuf = input.byteBuf
        logger.trace { "Response: ${byteBuf.readableBytes()}, ${byteBuf.readableArray().contentToString()}" }
        Chunker.Default.join(responseBuffer, byteBuf)
        if (responseBuffer.readableBytes() < FileResponse.HEADER_LENGTH + Archive.HEADER_LENGTH) {
            logger.trace { "Not enough data to read headers" }
            return
        }
        val response = FileResponse(responseBuffer)
        check(responses.contains(response.archiveId)) { "Unrequested response: ${response.archiveId}" }
        if (!response.archive.done) {
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
                false -> { closeQuietly(); socketFuture.completeExceptionally(it.cause()) }
            }
        }
        return socketFuture.get()
    }

    override fun getArchive(archiveId: ArchiveId): Archive {
        return request(archiveId).get().archive
    }

    override fun getIndexReferenceArchive(index: Int): Archive {
        return getArchive(ArchiveId(REFERENCE_INDEX, index))
    }

    override fun getReference(): CacheReference {
        return CacheReference.read(getArchive(CHECKSUM_ARCHIVE).data)
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