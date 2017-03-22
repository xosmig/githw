package com.xosmig.githw.utils

import org.junit.Assert.*
import org.junit.Test

class Sha256Test {

    @Test(expected = IllegalArgumentException::class)
    fun fromStringFailLength() {
        val builder = StringBuilder()
        for (i in 1..63) {
            builder.append("a")
        }
        Sha256.fromString(builder.toString())
    }

    @Test(expected = IllegalArgumentException::class)
    fun fromStringFailAlphabet() {
        val builder = StringBuilder()
        for (i in 1..63) {
            builder.append('a')
        }
        builder.append('q')
        Sha256.fromString(builder.toString())
    }

    @Test
    fun fromStringCorrect() {
        val builder = StringBuilder()
        for (i in 1..64) {
            builder.append("a")
        }
        Sha256.fromString(builder.toString())
    }
}
