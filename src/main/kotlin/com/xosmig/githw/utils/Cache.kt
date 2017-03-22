package com.xosmig.githw.utils

import kotlin.reflect.KProperty

interface Cache<out T> {

    /**
     * Current cache version.
     */
    val version: Int

    /**
     * Evaluated value.
     *
     * Access may cause calculation of some dependencies and the value itself.
     */
    val value: T

    /**
     * Evaluate value.
     *
     * May cause calculation of some dependencies and the value itself.
     */
    fun update()

    /**
     * Operator for delegating.
     */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    /**
     * Reset value. Causes reset of all depended caches.
     */
    fun reset()
}

/**
 * Used to work with lazy values which have dependencies over each other.
 *
 * If any dependency was updated / reset, this one will be updated on the next access.
 * Doesn't work properly in case of cyclic dependencies.
 */
fun<T> cache(vararg dependencies: Cache<*>, supplier: () -> T): Cache<T> = CacheSynchronized(supplier, *dependencies)

private class CacheSynchronized<out T>(private val supplier: () -> T, vararg dependencies: Cache<*>): Cache<T> {

    private val deps = dependencies.asList()
    private val depsVersions = ( IntArray(deps.size) { deps[it].version } ).toMutableList()
    private var result: T? = null

    override var version = 0
        private set
        @Synchronized get

    override val value: T
        @Synchronized
        get() {
            update()
            return result!!
        }

    @Synchronized
    override fun update() {
        var upToDate = true
        for ((i, dep) in deps.withIndex()) {
            dep.update()
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

    @Synchronized
    override fun reset() {
        result = null
    }
}
