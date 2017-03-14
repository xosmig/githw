package com.xosmig.githw.utils

fun <T> Iterable<T>.isEmpty(): Boolean {
    return !this.iterator().hasNext()
}

fun <T> Iterable<T>.isNotEmpty(): Boolean {
    return this.iterator().hasNext()
}
