package com.xosmig.githw.objects

import com.xosmig.githw.utils.Sha256
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Path

abstract class GitObjectLoaded(gitDir: Path): GitObject(gitDir) {
    override fun writeToDisk() {
        Files.newOutputStream(getObjectFile()).use {
            ObjectOutputStream(it).use {
                it.writeObject(javaClass.name)
                writeContentTo(it)
            }
        }
    }

    override final fun getSha256(): Sha256 {
        ByteArrayOutputStream().use {
            val baos = it
            ObjectOutputStream(baos).use {
                writeContentTo(it)
            }
            return Sha256.get(baos.toByteArray())
        }
    }

    override final val loaded: GitObjectLoaded
        get() = this

    abstract protected fun writeContentTo(out: ObjectOutputStream)
}
