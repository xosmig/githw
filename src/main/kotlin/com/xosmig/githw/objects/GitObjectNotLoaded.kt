package com.xosmig.githw.objects

import com.xosmig.githw.HASH_PREF_LENGTH
import java.io.IOError
import java.io.ObjectInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class GitObjectNotLoaded(private val sha256: String, private val objectsDir: Path): GitObject() {
    override fun sha256(): String = sha256

    @Throws(IOError::class)
    override fun writeToDisk(objectsDir: Path) = Unit

    @Throws(IOError::class)
    override fun load(): GitObject {
        val path = objectsDir
                .resolve(sha256.take(HASH_PREF_LENGTH))
                .resolve(sha256.drop(HASH_PREF_LENGTH))
        Files.newInputStream(path).use {
            ObjectInputStream(it).use {
                return it.readObject() as GitObject
            }
        }
    }
}
