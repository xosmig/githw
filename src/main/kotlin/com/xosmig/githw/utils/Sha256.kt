package com.xosmig.githw.utils

import org.apache.commons.codec.digest.DigestUtils
import java.io.Serializable

/**
 * Type-safe wrapper for getSha256 hash.
 */
class Sha256 private constructor(val value: String): Serializable {
    companion object {
        val HASH_PREF_LENGTH = 2

        fun get(content: ByteArray): Sha256 = Sha256(DigestUtils.sha256Hex(content))
    }

    fun pref(): String = value.take(HASH_PREF_LENGTH)

    fun suf(): String = value.drop(HASH_PREF_LENGTH)
}
