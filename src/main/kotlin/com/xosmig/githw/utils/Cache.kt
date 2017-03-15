package com.xosmig.githw.utils

import kotlin.reflect.KProperty

class Cache<out T>(private val supplier: () -> T, vararg deps: Cache<*>) {

    private val deps = deps.asList()
    private val depsVersions = (IntArray(deps.size) { deps[it].version }).toMutableList()
    private var version = 0
    private var result: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        evaluate()
        return result!!
    }

    fun evaluate() {
        var upToDate = true
        for (i in deps.indices) {
            deps[i].evaluate()
            if (deps[i].version != depsVersions[i]) {
                depsVersions[i] = deps[i].version
                upToDate = false
            }
        }
        if (result == null || !upToDate) {
            version += 1
            result = supplier()
        }
    }

    fun reset() {
        result = null
    }
}
