package com.runesuite.cache.content

import java.util.*
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties

object StringHashes {

    val known: Map<Int, String>

    init {
        val strings = ArrayList<String>()
        Index::class.nestedClasses.forEach { k ->
            k.objectInstance?.let { instance ->
                instance::class.declaredMemberProperties.filter { it.returnType == String::class.createType() }.forEach { p ->
                    strings.add(p.getter.call(instance) as String)
                }
                instance::class.declaredMemberFunctions.forEach { f ->
                    when (f.parameters.size) {
                        2 -> {
                            for (i in 0..128) {
                                strings.add(f.call(instance, i) as String)
                            }
                        }
                        3 -> {
                            for (i in 0..128) {
                                for (j in 0..128) {
                                    strings.add(f.call(instance, i, j) as String)
                                }
                            }
                        }
                    }
                }
            }
        }
        known = strings.distinct().associate { it.hashCode() to it }
    }
}