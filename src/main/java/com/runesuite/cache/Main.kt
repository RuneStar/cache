package com.runesuite.cache

import com.runesuite.cache.format.net.NetStore
import io.netty.handler.codec.MessageToByteEncoder
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.handler.traffic.ChannelTrafficShapingHandler

fun main(args: Array<String>) {
    NetStore.open().use {
//        it.request(FileRequest(255, 13))
//        it.request(FileRequest(255, 8))
//        it.request(FileRequest(255, 9))
//        it.request(FileRequest(255, 10))
//        it.request(FileRequest(255, 11))
//        it.request(FileRequest(255, 11))
//        it.request(FileRequest(255, 13))
//        it.getVolume0(255, 13)
//        it.getIndexReference(13)
        it.getReference()
        it.getIndexReference(1)
        it.getIndexReference(11)
        Thread.sleep(15_000)
    }


}