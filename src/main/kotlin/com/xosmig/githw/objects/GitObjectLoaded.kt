package com.xosmig.githw.objects

import com.xosmig.githw.utils.Sha256
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Path

abstract class GitObjectLoaded internal constructor(gitDir: Path, protected var onDisk: Boolean): GitObject(gitDir) {

    override val sha256: Sha256 by lazy {
        ByteArrayOutputStream().use {
            val byteStream = it
            ObjectOutputStream(byteStream).use {
                writeContentTo(it)
            }
            Sha256.get(byteStream.toByteArray())
        }
    }

    override final fun writeToDisk() {
        if (!onDisk) {
            writeToDiskImpl()
        }
    }

    open fun writeToDiskImpl() {
        Files.newOutputStream(getObjectFile()).use {
            ObjectOutputStream(it).use {
                it.writeObject(javaClass.name)
                writeContentTo(it)
            }
        }
    }

    override final val loaded: GitObjectLoaded
        get() = this

    abstract protected fun writeContentTo(out: ObjectOutputStream)
}
