package com.xosmig.githw.utils

import kotlin.reflect.KProperty

/**
 * Used to work with lazy values which have dependencies over each other.
 *
 * If any dependency was updated / reset, this one will be updated on the next access.
 * Doesn't work properly in case of cyclic dependencies.
 */
class Cache<out T>(private val supplier: () -> T, vararg dependencies: Cache<*>) {

    private val deps = dependencies.asList()
    private val depsVersions = ( IntArray(deps.size) { deps[it].version } ).toMutableList()
    private var version = 0
    private var result: T? = null

    /**
     * Evaluated value.
     *
     * Access may cause calculation of some dependencies and the value itself.
     */
    val value: T
        @Synchronized
        get() {
            evaluate()
            return result!!
        }

    /**
     * Operator for delegating.
     */
    @Synchronized
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    /**
     * Evaluate value.
     *
     * May cause calculation of some dependencies and the value itself.
     */
    @Synchronized
    fun evaluate() {
        var upToDate = true
        for ((i, dep) in deps.withIndex()) {
            dep.evaluate()
            if (dep.version != depsVersions[i]) {
                depsVersions[i] = dep.version
                upToDate = false
            }
        }
        if (result == null || !upToDate) {
            version += 1
            result = supplier()
        }
    }

    /**
     * Reset value. Causes reset of all depended caches.
     */
    @Synchronized
    fun reset() {
        result = null
    }
}
