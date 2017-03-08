package com.xosmig.githw.objects

import com.xosmig.githw.HASH_PREF_LENGTH
import org.apache.commons.codec.digest.DigestUtils
import java.io.DataOutputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Path

class GitFile(private val content: ByteArray): GitObject() {
    override fun sha256(): String = DigestUtils.sha256Hex(content)

    override fun writeToDisk(objectsDir: Path) {
        val sha256 = sha256()
        val dir = objectsDir.resolve(sha256.take(HASH_PREF_LENGTH))
        Files.createDirectory(dir)
        Files.newOutputStream(dir.resolve(sha256.drop(HASH_PREF_LENGTH))).use {
            ObjectOutputStream(it).use {
                it.writeObject(this)
            }
        }
    }
}
