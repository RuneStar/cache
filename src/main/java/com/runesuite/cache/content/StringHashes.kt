package com.runesuite.cache.content

import java.util.*
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties

object StringHashes {

    val known: Map<Int, String>

    init {
        val strings = ArrayList<String>()
        Index::class.nestedClasses.forEach { k ->
            k.objectInstance?.let { instance ->
                instance::class.declaredMemberProperties.filter { it.returnType == String::class.createType() }.forEach { p ->
                    val s = p.getter.call(instance) as String
                    strings.add(s)
                }
            }
        }
        val map = HashMap<Int, String>()
        strings.forEach {
            val hash = it.hashCode()
//            check(hash !in map)
            map.put(hash, it)
        }
        for (x in 0..256) {
            for (y in 0..256) {
                val m = Index.LANDSCAPES.map(x, y)
                val l = Index.LANDSCAPES.land(x, y)
                val mh = m.hashCode()
                val lh = l.hashCode()
                check (mh !in map)
                check (lh !in map)
                map.put(mh, m)
                map.put(lh, l)
            }
        }
        known = map
    }
}