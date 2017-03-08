package com.xosmig.githw.objects

import org.apache.commons.codec.digest.DigestUtils
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Path

class GitFile(gitDir: Path, private val content: ByteArray): GitObject(gitDir) {
    companion object {
        fun load(gitDir: Path, ins: ObjectInputStream): GitFile = GitFile(gitDir, ins.readObject() as ByteArray)
    }

    override fun sha256(): String = DigestUtils.sha256Hex(content)

    override val loaded: GitObject = this

    override fun writeToDisk() {
        Files.newOutputStream(getObjectFile()).use {
            ObjectOutputStream(it).use {
                it.writeObject(javaClass.name)
                it.writeObject(content)
            }
        }
    }
}
