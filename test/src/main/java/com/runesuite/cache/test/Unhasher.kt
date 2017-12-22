package com.runesuite.cache.test

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder

fun unhashStrings(tokens: Set<String>, maxCombinations: Int, hashes: Set<Int>): Multimap<Int, String> {
    val tokensArray = tokens.filter { it.isNotEmpty() }.map { it.toCharArray() }.toTypedArray()
    val results = MultimapBuilder.hashKeys().arrayListValues().build<Int, String>()
    unhash0(IntArray(maxCombinations), 0, 0, tokensArray, hashes.toHashSet(), results)
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
            unhash0(currentTokens, currentLength + 1, newHash * 31, tokens, hashes, results)
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
    var h = hash + chars[0].toInt()
    for (i in 1 until chars.size) {
        h = 31 * h + chars[i].toInt()
    }
    return h
}