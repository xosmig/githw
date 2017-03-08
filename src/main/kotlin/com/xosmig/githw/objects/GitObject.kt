package com.xosmig.githw.objects

import com.xosmig.githw.HASH_PREF_LENGTH
import com.xosmig.githw.OBJECTS_PATH
import java.io.IOError
import java.nio.file.Files
import java.nio.file.Path

abstract class GitObject(protected val gitDir: Path) {

    protected val objectsDir: Path
            get() = gitDir.resolve(OBJECTS_PATH)

    protected fun getObjectFile(): Path {
        val sha256 = sha256()
        val dir = objectsDir.resolve(sha256.take(HASH_PREF_LENGTH))
        Files.createDirectory(dir)
        return dir.resolve(sha256.drop(HASH_PREF_LENGTH))
    }

    /** Write all changes in this object and all sub-objects to disk. */
    @Throws(IOError::class)
    abstract fun writeToDisk()

    abstract fun sha256(): String

    abstract val loaded: GitObject
}
