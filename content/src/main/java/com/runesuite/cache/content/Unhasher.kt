package com.runesuite.cache.content

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder

fun unhashStrings(tokens: Set<String>, maxCombinations: Int, hashes: Set<Int>): Multimap<Int, String> {
    val results = MultimapBuilder.hashKeys().arrayListValues().build<Int, String>()
    unhash0(IntArray(maxCombinations), 0, 0, tokens.map { it.toCharArray() }.toTypedArray(), hashes.toHashSet(), results)
    return results
}

private fun unhash0(
        currentTokens: IntArray,
        currentLength: Int,
        currentHash: Int,
        tokens: Array<CharArray>,
        hashes: HashSet<Int>,
        results: Multimap<Int, String>
) {
    for (i in tokens.indices) {
        currentTokens[currentLength] = i
        val newHash = hashAppend(currentHash, tokens[i])
        if (newHash in hashes) {
            val string = buildString(currentTokens, currentLength + 1, tokens)
            results.put(newHash, string)
            println(string)
        }
        if (currentLength + 1 < currentTokens.size) {
            unhash0(currentTokens, currentLength + 1, newHash, tokens, hashes, results)
        }
    }
}

private fun buildString(currentTokens: IntArray, currentLength: Int, tokens: Array<CharArray>): String {
    val sb = StringBuilder()
    for (i in 0 until currentLength) {
        sb.append(tokens[currentTokens[i]])
    }
    return sb.toString()
}

private fun hashAppend(hash: Int, chars: CharArray): Int {
    var h = hash
    for (c in chars) {
        h = 31 * h + c.toInt()
    }
    return h
}