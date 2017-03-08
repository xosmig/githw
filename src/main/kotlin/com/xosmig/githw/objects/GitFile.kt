package com.xosmig.githw.objects

import org.apache.commons.codec.digest.DigestUtils
import java.nio.file.Files
import java.nio.file.Path

abstract class GitFile: GitObject()

class GitFileNotLoaded(private val sha256: String): GitFile() {
    override fun sha256(): String = sha256

    override fun writeToDisk(objectsDir: Path) = Unit
}

class GitFileLoaded(private val content: ByteArray): GitFile() {
    override fun sha256(): String = DigestUtils.sha256Hex(content)

    override fun writeToDisk(objectsDir: Path) {
        val sha256 = sha256()
        val dir = objectsDir.resolve(sha256.take(2))
        Files.createDirectory(dir)
        Files.write(dir.resolve(sha256.drop(2)), content)
    }
}
