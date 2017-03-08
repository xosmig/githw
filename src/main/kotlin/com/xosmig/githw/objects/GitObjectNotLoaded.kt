package com.xosmig.githw.objects

import java.io.IOError
import java.io.ObjectInputStream
import java.nio.file.Files
import java.nio.file.Path

class GitObjectNotLoaded(gitDir: Path, private val sha256: String): GitObject(gitDir) {
    override fun sha256(): String = sha256

    @Throws(IOError::class)
    override fun writeToDisk() = Unit

    override val loaded by lazy {
        Files.newInputStream(getObjectFile()).use {
            ObjectInputStream(it).use {
                val type = it.readObject() as String
                when (type) {
                    GitFile::class.java.name -> GitFile.load(gitDir, it)
                    GitTree::class.java.name -> GitTree.load(gitDir, it)
                    else -> throw ClassNotFoundException("'$type' is not a valid GitObject class")
                }
            }
        }
    }
}
