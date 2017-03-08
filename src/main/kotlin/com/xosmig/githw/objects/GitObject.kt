package com.xosmig.githw.objects

import java.io.IOError
import java.nio.file.Path

abstract class GitObject {
    /** Write all changes in this object and all sub-objects to disk. */
    @Throws(IOError::class)
    abstract fun writeToDisk(objectsDir: Path)

    abstract fun sha256(): String

    @Throws(IOError::class)
    open fun load(): GitObject = this
}
