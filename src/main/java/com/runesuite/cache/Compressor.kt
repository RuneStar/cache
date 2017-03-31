package com.runesuite.cache

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import java.io.ByteArrayOutputStream

enum class Compressor(val id: Int) {

    NONE(0) {
        override fun compress(byteArray: ByteArray): ByteArray {
            return byteArray
        }

        override fun decompress(byteArray: ByteArray): ByteArray {
            return byteArray
        }
    },

    BZIP2(1) {
        private val HEADER = byteArrayOf('B'.toByte(), 'Z'.toByte(), 'h'.toByte(), 1)

        override fun compress(byteArray: ByteArray): ByteArray {
            byteArray.inputStream().use { inputStream ->
                val bout = ByteArrayOutputStream()
                BZip2CompressorOutputStream(bout).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                val full =  bout.toByteArray()
                val header = full.copyOf(HEADER.size)
                check(header.contentEquals(HEADER)) { header.contentToString() }
                return full.copyOfRange(HEADER.size, full.size)
            }
        }

        override fun decompress(byteArray: ByteArray): ByteArray {
            val data = HEADER.plus(byteArray)
            BZip2CompressorInputStream(data.inputStream()).use {
                return it.readBytes()
            }
        }
    },

    GZIP(2) {
        override fun compress(byteArray: ByteArray): ByteArray {
            byteArray.inputStream().use { inputStream ->
                val bout = ByteArrayOutputStream()
                GzipCompressorOutputStream(bout).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                return bout.toByteArray()
            }
        }

        override fun decompress(byteArray: ByteArray): ByteArray {
            GzipCompressorInputStream(byteArray.inputStream()).use {
                return it.readBytes()
            }
        }
    };

    abstract fun decompress(byteArray: ByteArray): ByteArray

    abstract fun compress(byteArray: ByteArray): ByteArray

    companion object {
        val LOOKUP = values().associateBy { it.id }
    }
}