package com.xosmig.githw.utils

/**
 * Returns true if this iterable contains no elements.
 */
fun <T> Iterable<T>.isEmpty(): Boolean {
    return !this.iterator().hasNext()
}

/**
 * Returns true if this iterable contains some elements.
 */
fun <T> Iterable<T>.isNotEmpty(): Boolean {
    return this.iterator().hasNext()
}
