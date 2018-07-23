package org.runestar.cache.test

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import org.runestar.cache.content.def.CHARSET

fun unhashStrings(
        tokens: Set<String>,
        prefix: String?,
        suffix: String?,
        maxCombinations: Int,
        hashes: Set<Int>
): Multimap<Int, String> {
    val tokensArray = tokens.filter { it.isNotEmpty() }.map { it.toByteArray(CHARSET) }.toTypedArray()
    val prefix0 = if (prefix != null) prefix.toByteArray(CHARSET) else null
    val prefixHash = if (prefix != null) hashAppend(0, prefix0!!) * 31 else 0
    val suffix0 = if (suffix != null) suffix.toByteArray(CHARSET) else null
    val results = MultimapBuilder.hashKeys().arrayListValues().build<Int, String>()
    unhash0(IntArray(maxCombinations), 0, prefixHash, tokensArray, prefix0, suffix0, hashes.toHashSet(), results)
    return results
}

private fun unhash0(
        currentTokens: IntArray,
        currentLength: Int,
        currentHash: Int,
        tokens: Array<ByteArray>,
        prefix: ByteArray?,
        suffix: ByteArray?,
        hashes: HashSet<Int>,
        results: Multimap<Int, String>
) {
    for (i in tokens.indices) {
        currentTokens[currentLength] = i
        val newHash = hashAppend(currentHash, tokens[i])
        val newHash2 = if (suffix == null) newHash else hashAppend(newHash * 31, suffix)
        if (newHash2 in hashes) {
            val string = buildString(prefix, suffix, currentTokens, currentLength + 1, tokens)
            results.put(newHash2, string)
            println(string)
        }
        if (currentLength + 1 < currentTokens.size) {
            unhash0(currentTokens, currentLength + 1, newHash * 31, tokens, prefix, suffix, hashes, results)
        }
    }
}

private fun buildString(
        prefix: ByteArray?,
        suffix: ByteArray?,
        currentTokens: IntArray,
        currentLength: Int,
        tokens: Array<ByteArray>
): String {
    val sb = StringBuilder()
    if (prefix != null) sb.append(prefix.toString(CHARSET))
    for (i in 0 until currentLength) {
        sb.append(tokens[currentTokens[i]].toString(CHARSET))
    }
    if (suffix != null) sb.append(suffix.toString(CHARSET))
    return sb.toString()
}

private fun hashAppend(hash: Int, chars: ByteArray): Int {
    var h = hash + chars[0]
    for (i in 1 until chars.size) {
        h = 31 * h + chars[i]
    }
    return h
}