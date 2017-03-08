package com.xosmig.githw.objects

import java.nio.file.Path

abstract class GitFile: GitObject()

class GitFileNotLoaded(private val sha256: String): GitFile() {
    override fun sha256(): String = sha256
    override fun writeToDisk(objectsDir: Path) = Unit
}

class GitFileLoaded(private val content: ByteArray): GitFile() {
    override fun writeToDisk(objectsDir: Path) {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }

    override fun sha256(): String {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }
}
