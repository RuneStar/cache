package com.runesuite.cache

import com.runesuite.cache.format.fs.FileSystemStore
import com.runesuite.cache.format.net.NetStore
import io.netty.handler.codec.MessageToByteEncoder
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.handler.traffic.ChannelTrafficShapingHandler

fun main(args: Array<String>) {


    FileSystemStore.open().use {
        println(it.getReference().join())
    }

    // file
    // volume
    // volume
    // record
}