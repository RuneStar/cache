package com.runesuite.cache.format.net

import com.runesuite.cache.extensions.closeQuietly
import com.runesuite.cache.extensions.connectBlocking
import com.runesuite.cache.extensions.readableArray
import com.runesuite.cache.format.CacheReference
import com.runesuite.cache.format.IndexReference
import com.runesuite.cache.format.ReadableCache
import com.runesuite.cache.format.Volume
import com.runesuite.general.RuneScape
import io.netty.buffer.Unpooled
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import mu.KotlinLogging
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

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
        private val KEEP_ALIVE_INTERVAL_MS = 10_000L
        private val PING_REQUEST = ConnectionInfoOffer(ConnectionInfoOffer.State.LOGGED_OUT)

        fun default(): NetClientCache {
            return NetClientCache(RuneScape.revision, RuneScape.suggestedHost(), RuneScape.PORT)
        }
    }

    private val logger = KotlinLogging.logger {  }

    private var isOpen = true

    override fun isOpen() = isOpen

    var revision: Int
        private set

    private val vertx = Vertx.vertx()

    private var socket: NetSocket

    @Volatile
    private var active: PendingFile? = null

    private val requestBuffer = Unpooled.buffer(5)

    private var responseBuffer = Unpooled.compositeBuffer()

    private val idleExecutor = ScheduledThreadPoolExecutor(1)

    @Volatile
    private lateinit var idle: ScheduledFuture<*>

    init {
        val netClient = vertx.createNetClient()
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
        logger.debug { "Handshake complete: revision $revision" }
        ping()
        idleExecutor.removeOnCancelPolicy = true
        startIdle()
        socket.handler { onSocketRead(it) }
    }

    private fun startIdle() {
        logger.trace { "Start idle" }
        idle = idleExecutor.scheduleAtFixedRate({ ping() },
                KEEP_ALIVE_INTERVAL_MS, KEEP_ALIVE_INTERVAL_MS, TimeUnit.MILLISECONDS)
    }

    private fun stopIdle() {
        logger.trace { "Stop idle" }
        idle.cancel(false)
    }

    private fun onSocketRead(input: Buffer) {
        val byteBuf = input.byteBuf
        logger.debug { "Response size: ${byteBuf.readableBytes()}" }
        logger.trace { "Response: ${byteBuf.readableArray().contentToString()}" }
        logger.trace { "Response buffer: ${responseBuffer.readableArray().contentToString()}" }
        Chunker.Default.join(responseBuffer, byteBuf)
        val response = FileResponse(responseBuffer)
        if (!response.done) {
            return
        }
        val act = checkNotNull(active)
        check(act.request.index == response.index)
        check(act.request.archive == response.archive)
        logger.debug { "Done: $response" }
        active = null
        act.response.complete(response)
        responseBuffer = Unpooled.compositeBuffer()
        startIdle()
    }

    private fun ping() {
        logger.debug { "ping" }
        write(PING_REQUEST)
    }

    private fun write(request: Request) {
        request.write(requestBuffer.clear())
        logger.trace { "Writing: ${requestBuffer.readableArray().contentToString()}" }
        socket.write(Buffer.buffer(requestBuffer))
    }

    // can only make new request after previous has completed
    private fun request(index: Int, archive: Int): CompletableFuture<FileResponse> {
        check(isOpen)
        val req = PendingFile(FileRequest(index, archive), CompletableFuture<FileResponse>())
        logger.debug { req.request }
        check(active == null)
        active = req
        stopIdle()
        write(req.request)
        return req.response
    }

    private fun handshake(revision: Int): HandshakeResponse {
        logger.debug { "Handshaking: revision $revision" }
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

    override fun getVolume(index: Int, archive: Int): Volume {
        return request(index, archive).get().data
    }

    override fun getIndexReference(index: Int): IndexReference {
        return IndexReference(getVolume(REFERENCE_INDEX, index))
    }

    override fun getReference(): CacheReference {
        return CacheReference.read(getVolume(REFERENCE_INDEX, REFERENCE_ARCHIVE).decompressed)
    }

    override fun close() {
        if (isOpen) {
            isOpen = false
            idleExecutor.shutdown()
            vertx.close()
        }
    }

    private data class PendingFile(val request: FileRequest, val response: CompletableFuture<FileResponse>)
}