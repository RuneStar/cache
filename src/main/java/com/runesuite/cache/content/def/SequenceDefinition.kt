package com.runesuite.cache.content.def

import com.fasterxml.jackson.annotation.JsonInclude
import com.hunterwb.kxtra.collections.intarray.transform
import io.netty.buffer.ByteBuf

class SequenceDefinition : CacheDefinition() {

    var frameIds: IntArray? = null
    var field3048: IntArray? = null
    var frameLengths: IntArray? = null
    var rightHandItem = -1
    var interleaveLeave: IntArray? = null
    var stretches = false
    var forcedPriority = 5
    var maxLoops = 99
    var field3056: IntArray? = null
    var precedenceAnimating = -1
    var leftHandItem = -1
    var replyMode = 2
    var frameStep = -1
    var priority = -1

    override fun read(buffer: ByteBuf) {
        while (true) {
            val opcode = buffer.readUnsignedByte().toInt()
            when (opcode) {
                0 -> return
                1 -> {
                    val length = buffer.readUnsignedShort()
                    frameLengths = IntArray(length) { buffer.readUnsignedShort() }
                    frameIds = IntArray(length) { buffer.readUnsignedShort() }
                    frameIds!!.transform { it + (buffer.readUnsignedShort() shl 16) }
                }
                2 -> frameStep = buffer.readUnsignedShort()
                3 -> {
                    val length = buffer.readUnsignedByte().toInt()
                    interleaveLeave = IntArray(length + 1) {
                        if (it == length) 9999999
                        else buffer.readUnsignedByte().toInt()
                    }
                }
                4 -> stretches = true
                5 -> forcedPriority = buffer.readUnsignedByte().toInt()
                6 -> leftHandItem = buffer.readUnsignedShort()
                7 -> rightHandItem = buffer.readUnsignedShort()
                8 -> maxLoops = buffer.readUnsignedByte().toInt()
                9 -> precedenceAnimating = buffer.readUnsignedByte().toInt()
                10 -> priority = buffer.readUnsignedByte().toInt()
                11 -> replyMode = buffer.readUnsignedByte().toInt()
                12 -> {
                    val length = buffer.readUnsignedByte().toInt()
                    field3048 = IntArray(length) { buffer.readUnsignedShort() }
                    field3048!!.transform { it + (buffer.readUnsignedShort() shl 16) }
                }
                13 -> {
                    val length = buffer.readUnsignedByte().toInt()
                    field3056 = IntArray(length) { buffer.readUnsignedMedium() }
                }
                else -> error(opcode)
            }
        }
    }
}