package com.runesuite.cache.format.net

import com.runesuite.cache.format.CompressedVolume
import com.runesuite.cache.format.Volume
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.util.internal.logging.InternalLoggerFactory
import java.io.IOException
import java.nio.channels.ClosedChannelException
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CompletableFuture
import kotlin.reflect.jvm.jvmName

internal class NetStoreConnection(
        group: EventLoopGroup,
        host: String,
        port: Int,
        revision: Int
) : java.nio.channels.Channel {

    private val bootstrap = Bootstrap()
            .group(group)
            .channel(NioSocketChannel::class.java)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline()
                            .addLast(LoggingHandler("${NetStore::class.jvmName}+raw", LogLevel.DEBUG))
                            .addLast(DECODER_KEY, HandshakeResponse.Decoder())
                            .addLast(ENCODER_KEY, HandshakeRequest.Encoder())
                            .addLast(LoggingHandler("${NetStore::class.jvmName}+obj", LogLevel.DEBUG))
                            .addLast(HANDLER_KEY, HandshakeResponseHandler())
                            .addLast(ExceptionHandler())
                }
            })

    private val channel: Channel

    private val connected = CompletableFuture<Unit>()

    private val activeRequests: BlockingQueue<PendingFileRequest> = ArrayBlockingQueue(MAX_ACTIVE_REQUESTS)

    init {
        channel = bootstrap.connect(host, port).sync().channel()
        channel.writeAndFlush(HandshakeRequest(15, revision))
        connected.join()
    }

    override fun close() {
        channel.close().sync()
    }

    override fun isOpen(): Boolean {
        return channel.isOpen
    }

    @Synchronized
    fun requestFile(index: Int, volume: Int): CompletableFuture<Volume> {
        if (!isOpen) throw ClosedChannelException()
        val future = CompletableFuture<Volume>()
        val fileRequest = FileRequest(index, volume)
        activeRequests.put(PendingFileRequest(fileRequest, future))
        channel.writeAndFlush(fileRequest)
        return future
    }

    companion object {
        private const val ENCODER_KEY = "encoder"
        private const val DECODER_KEY = "decoder"
        private const val HANDLER_KEY = "handler"

        private const val MAX_ACTIVE_REQUESTS = 19
    }

    private class PendingFileRequest(val request: FileRequest, val response: CompletableFuture<Volume>)

    private inner class HandshakeResponseHandler : SimpleChannelInboundHandler<HandshakeResponse>() {

        override fun channelRead0(ctx: ChannelHandlerContext, msg: HandshakeResponse) {
            if (msg != HandshakeResponse.SUCCESS) {
                close()
                throw IOException("unsuccessful handshake response: $msg")
            }
            ctx.pipeline().replace(ENCODER_KEY, ENCODER_KEY, ConnectionInfoOffer.Encoder())
            ctx.pipeline().replace(DECODER_KEY, DECODER_KEY, FileResponse.Decoder())
            ctx.pipeline().addFirst(FileResponse.FrameDecoder())
            ctx.writeAndFlush(ConnectionInfoOffer(3, 0)).addListener {
                ctx.pipeline().replace(ENCODER_KEY, ENCODER_KEY, FileRequest.Encoder())
                ctx.pipeline().replace(HANDLER_KEY, HANDLER_KEY, FileResponseHandler())
                connected.complete(Unit)
            }
        }
    }

    private inner class FileResponseHandler : SimpleChannelInboundHandler<FileResponse>() {

        override fun channelRead0(ctx: ChannelHandlerContext, msg: FileResponse) {
            val pfr = activeRequests.remove()
            check(msg.index == pfr.request.index && msg.volume == pfr.request.volume)
            pfr.response.complete(CompressedVolume(msg.data))
        }
    }

    private inner class ExceptionHandler : ChannelDuplexHandler() {

        override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
            InternalLoggerFactory.getInstance(javaClass).error(cause)
            close()
        }
    }
}