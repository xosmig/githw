package com.xosmig.githw.utils

import java.util.stream.Collectors
import java.util.stream.Stream

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

fun <T> Stream<T>.toList(): List<T> = this.collect(Collectors.toList())
