package com.runesuite.cache.content

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors

interface StringUnhasher {

    fun unhash(targetHashes: Set<Int>): Multimap<Int, String>

    class CharPool(pool: Set<Char>, val maxLength: Int, val threads: Int): StringUnhasher {
        private val pool = pool.toCharArray()

        override fun unhash(targetHashes: Set<Int>): Multimap<Int, String> {
            val ex = Executors.newFixedThreadPool(threads)
            val ecs = ExecutorCompletionService<Multimap<Int, String>>(ex)
            val poolSplit = pool.withIndex().groupBy({ it.index % threads }) { it.value }
            for (thread in 0 until threads) {
                ecs.submit {
                    val threadStrings = ArrayListMultimap.create<Int, String>()
                    val threadString = CharArray(maxLength)
                    poolSplit.getValue(thread).forEach { c ->
                        threadString[0] = c
                        unhash0(c.toInt() * 31, threadString, 2, threadStrings, targetHashes)
                    }
                    threadStrings
                }
            }
            val strings = ArrayListMultimap.create<Int, String>()
            for (thread in 0 until threads) {
                strings.putAll(ecs.take().get())
            }
            ex.shutdown()
            return strings
        }

        private fun unhash0(currentHash: Int, string: CharArray, length: Int, strings: Multimap<Int, String>, targetHashes: Set<Int>) {
            pool.forEach { c ->
                string[length - 1] = c
                val hash = currentHash + c.toInt()
                if (hash in targetHashes) {
                    val s = String(string, 0, length)
                    strings.put(hash, s)
                }
                if (length != string.size) {
                    unhash0(hash * 31, string, length + 1, strings, targetHashes)
                }
            }
        }
    }

    class Dictionary(tokens: Set<String>, val maxTokenCount: Int) : StringUnhasher {
        private val tokens = tokens.toTypedArray()
        private val longestToken = this.tokens.map { it.length }.max()!!

        private fun updateHash(token: CharSequence, hash: Int): Int {
            var h = hash + token[0].toInt()
            for (i in 1 until token.length) {
                h = 31 * h + token[i].toInt()
            }
            return h
        }

        override fun unhash(targetHashes: Set<Int>): Multimap<Int, String> {
            val strings = ArrayListMultimap.create<Int, String>()
            unhash0(0, StringBuilder(longestToken * maxTokenCount), 1, strings, targetHashes)
            return strings
        }

        private fun unhash0(currentHash: Int, stringBuilder: StringBuilder, tokenCount: Int, strings: Multimap<Int, String>, targetHashes: Set<Int>) {
            tokens.forEach { t ->
                stringBuilder.append(t)
                val hash = updateHash(t, currentHash)
                if (hash in targetHashes) {
                    val s = stringBuilder.toString()
                    strings.put(hash, s)
                }
                if (tokenCount != maxTokenCount) {
                    unhash0(hash * 31, stringBuilder, tokenCount + 1, strings, targetHashes)
                }
                stringBuilder.setLength(stringBuilder.length - t.length)
            }
        }
    }
}