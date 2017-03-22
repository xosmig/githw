package com.xosmig.githw.utils

import org.apache.commons.codec.digest.DigestUtils
import java.io.SequenceInputStream
import java.io.Serializable

/**
 * Type-safe wrapper for sha256 hash.
 */
class Sha256 private constructor(val value: String): Serializable {

    companion object {
        val HASH_PREF_LENGTH = 2

        fun get(content: ByteArray): Sha256 = Sha256(DigestUtils.sha256Hex(content))

        fun get(content: String): Sha256 = get(content.toByteArray())

        fun fromString(str: String): Sha256 {
            if (!str.matches(Regex("[0-9a-f]{64}"))) {
                throw IllegalArgumentException("invalid sha256 hash: '$str'")
            }
            return Sha256(str)
        }
    }

    val pref: String
        get() = value.take(HASH_PREF_LENGTH)

    val suf: String
        get() = value.drop(HASH_PREF_LENGTH)

    fun add(content: ByteArray): Sha256 {
        val dataStream = SequenceInputStream(content.inputStream(), value.byteInputStream())
        return Sha256(DigestUtils.sha256Hex(dataStream))
    }

    fun add(content: String): Sha256 = add(content.toByteArray())

    fun add(other: Sha256): Sha256 = add(other.value)

    override fun equals(other: Any?): Boolean = other is Sha256 && value == other.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value
}
