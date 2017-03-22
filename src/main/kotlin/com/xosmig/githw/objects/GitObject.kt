package com.xosmig.githw.objects

import com.xosmig.githw.OBJECTS_PATH
import com.xosmig.githw.controller.GithwController
import com.xosmig.githw.utils.Sha256
import java.io.IOException
import java.nio.file.Files.*
import java.nio.file.Path

/**
 * Basic class for all git objects.
 *
 * Warning: All git objects are immutable and lazy.
 */
abstract class GitObject internal constructor(protected val githw: GithwController) {

    abstract val sha256: Sha256

    /**
     * Path to the directory which contains all git objects.
     */
    protected val objectsDir: Path
            get() = githw.gitDir.resolve(OBJECTS_PATH)

    /**
     * Path to git object on disk.
     */
    protected val objectFile: Path
            get() = run {
                val dir = objectsDir.resolve(sha256.pref)
                createDirectories(dir)
                return dir.resolve(sha256.suf)
            }

    /**
     * Write all changes in this object and all sub-objects to disk.
     */
    @Throws(IOException::class)
    abstract fun writeToDisk()

    /**
     * Provides lazy loading of git objects from disk.
     */
    abstract val loaded: GitObjectLoaded
}
