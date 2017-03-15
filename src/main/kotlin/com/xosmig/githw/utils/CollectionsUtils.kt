package com.xosmig.githw.utils

import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.reflect.KClass

/**
 * Returns true if this iterable contains no elements.
 */
fun <T> Iterable<T>.isEmpty(): Boolean {
    return !this.iterator().hasNext()
}

/**
 * Returns true if this iterable contains some elements.
 */
fun <T> Iterable<T>.isNotEmpty(): Boolean = !this.isEmpty()

fun <T> Stream<T>.toList(): List<T> = this.collect(Collectors.toList())

fun <T: Any> List<*>.checkContent(clazz: KClass<T>): List<T> {
    for (obj in this) {
        if (!clazz.isInstance(obj)) {
            val className = if (obj == null) { "null" } else { obj::class.qualifiedName }
            throw ClassCastException("$className cannot be cast to ${clazz.qualifiedName}")
        }
    }
    @Suppress("UNCHECKED_CAST")
    return this as List<T>
}
