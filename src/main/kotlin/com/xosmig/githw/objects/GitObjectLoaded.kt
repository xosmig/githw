package com.xosmig.githw.objects

import com.xosmig.githw.controller.GithwController
import com.xosmig.githw.utils.Sha256
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.nio.file.Files.*
import java.nio.file.Path

abstract class GitObjectLoaded internal constructor( githw: GithwController,
                                                     private val knownSha256: Sha256? ): GitObject(githw) {

    private var onDisk = knownSha256 != null

    override final val sha256: Sha256 by lazy {
        if (knownSha256 != null) {
            knownSha256
        }
        ByteArrayOutputStream().use {
            ObjectOutputStream(it).use {
                writeContentTo(it)
            }
//            println(it.toByteArray())
            Sha256.get(it.toByteArray())
        }
    }

    override final fun writeToDisk() {
        if (!onDisk) {
            writeToDiskImpl()
            onDisk = true
        }
    }

    open fun writeToDiskImpl() {
        newOutputStream(objectFile).use {
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
