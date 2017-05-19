package com.runesuite.cache.content

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors

interface StringUnhasher {

    fun unhash(targetHashes: Set<Int>, threads: Int): Multimap<Int, String>

    class CharPool(pool: Set<Char>, val maxLength: Int): StringUnhasher {
        private val pool = pool.toCharArray()

        override fun unhash(targetHashes: Set<Int>, threads: Int): Multimap<Int, String> {
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
            return token.fold(hash) { acc, c -> acc * 31 + c.toInt() }
        }

        override fun unhash(targetHashes: Set<Int>, threads: Int): Multimap<Int, String> {
            val strings = ArrayListMultimap.create<Int, String>()
            unhash0(0, StringBuilder(longestToken * maxTokenCount), 1, strings, targetHashes)
            return strings
        }

        private fun unhash0(currentHash: Int, string: StringBuilder, tokenCount: Int, strings: Multimap<Int, String>, targetHashes: Set<Int>) {
            tokens.forEach { t ->
                string.append(t)
                val hash = updateHash(t, currentHash)
                if (hash in targetHashes) {
                    val s = string.toString()
                    strings.put(hash, s)
                }
                if (tokenCount != maxTokenCount) {
                    unhash0(hash, string, tokenCount + 1, strings, targetHashes)
                }
                string.setLength(string.length - t.length)
            }
        }
    }
}