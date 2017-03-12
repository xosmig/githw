package com.xosmig.githw.objects

import com.xosmig.githw.OBJECTS_PATH
import com.xosmig.githw.utils.Sha256
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

abstract class GitObject(protected val gitDir: Path) {

    companion object {
        fun load(gitDir: Path, sha256: Sha256): GitObjectLoaded = GitObjectFromDisk(gitDir, sha256).loaded
    }

    protected val objectsDir: Path
            get() = gitDir.resolve(OBJECTS_PATH)

    protected fun getObjectFile(): Path {
        val sha256 = getSha256()
        val dir = objectsDir.resolve(sha256.pref())
        Files.createDirectories(dir)
        return dir.resolve(sha256.suf())
    }

    /** Write all changes in this object and all sub-objects to disk. */
    @Throws(IOException::class)
    abstract fun writeToDisk()

    abstract fun getSha256(): Sha256

    abstract val loaded: GitObjectLoaded
}
