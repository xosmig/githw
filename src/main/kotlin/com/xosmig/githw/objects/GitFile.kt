package com.xosmig.githw.objects

import java.nio.file.Path

class GitFile(val content: ByteArray) : GitObject() {
    override fun writeToDisk(objectsDir: Path) {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }
}
