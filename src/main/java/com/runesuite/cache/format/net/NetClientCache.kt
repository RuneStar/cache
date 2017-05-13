package com.runesuite.cache.format.net

import com.runesuite.cache.extensions.closeQuietly
import com.runesuite.cache.extensions.connectBlocking
import com.runesuite.cache.extensions.readableArray
import com.runesuite.cache.format.CacheReference
import com.runesuite.cache.format.Container
import com.runesuite.cache.format.IndexReference
import com.runesuite.cache.format.ReadableCache
import com.runesuite.general.RuneScape
import io.netty.buffer.Unpooled
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import mu.KotlinLogging
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future

class NetClientCache
@Throws(IOException::class)
constructor(
        revisionMinimum: Int,
        val host: String,
        val port: Int
) : ReadableCache() {

    companion object {
        private const val REFERENCE_INDEX = 255
        private const val REFERENCE_ARCHIVE = 255

        fun default(): NetClientCache {
            return NetClientCache(RuneScape.revision, "oldschool29.runescape.com", 43594)
        }
    }

    private val logger = KotlinLogging.logger {  }

    private var isOpen = true

    override fun isOpen() = isOpen

    var revision: Int
        private set

    private val vertx = Vertx.vertx()

    private val netClient = vertx.createNetClient()

    private var socket: NetSocket

    private val responses: MutableMap<Pair<Int, Int>, CompletableFuture<FileResponse>> = ConcurrentHashMap()

    private val requestBuffer = Unpooled.buffer(5)

    private var responseBuffer = Unpooled.compositeBuffer()

    init {
        revision = revisionMinimum - 1
        var handshakeStatus: HandshakeResponse.Status
        do {
            revision++
            try {
                socket = netClient.connectBlocking(port, host)
            } catch (connectException: IOException) {
                closeQuietly()
                throw connectException
            }
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
        val response = FileResponse(responseBuffer)
        if (!response.done) {
            return
        }
        val responseId = response.index to response.archive
        check(responses.contains(responseId)) { "Unrequested response: $response" }
        logger.trace { "Done: $response" }
        val responseFuture = responses.remove(responseId)!!
        responseFuture.complete(response)
        responseBuffer = Unpooled.compositeBuffer()
    }

    private fun write(request: Request) {
        requestBuffer.clear()
        request.write(requestBuffer)
        logger.trace { "Writing: ${requestBuffer.readableArray().contentToString()}" }
        socket.write(Buffer.buffer(requestBuffer))
    }

    fun request(index: Int, archive: Int): Future<FileResponse> {
        check(isOpen)
        val responseFuture = CompletableFuture<FileResponse>()
        val fileRequest = FileRequest(index, archive)
        logger.trace { fileRequest }
        responses[index to archive] = responseFuture
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

    override fun getContainer(index: Int, archive: Int): Container {
        return request(index, archive).get().data
    }

    override fun getIndexReference(index: Int): IndexReference {
        return IndexReference(getContainer(REFERENCE_INDEX, index))
    }

    override fun getReference(): CacheReference {
        return CacheReference.read(getContainer(REFERENCE_INDEX, REFERENCE_ARCHIVE).decompressed)
    }

    override fun close() {
        if (isOpen) {
            isOpen = false
            vertx.close()
        }
    }
}