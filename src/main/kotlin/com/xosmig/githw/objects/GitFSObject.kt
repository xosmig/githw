package com.xosmig.githw.objects

import java.nio.file.Path
import com.xosmig.githw.utils.Sha256
import java.nio.file.Files.exists
import java.util.*

abstract class GitFSObject internal constructor(gitDir: Path,
                                                knownSha256: Sha256? ): GitObjectLoaded(gitDir, knownSha256) {

    abstract fun revert(path: Path)

    protected abstract fun mergeWith(other: GitFSObject, path: Path, newFiles: MutableList<Path>)

    fun mergeWith(other: GitFSObject, path: Path): List<Path> {
        val newFiles = ArrayList<Path>()
        mergeWith(other, path, newFiles)
        return newFiles
    }

    // Should be in companion object, but it isn't supported yet
    protected fun resolveConflictPath(path: Path, newFiles: MutableList<Path>): Path {
        var i = 1
        while (true) {
            val newPath = path.parent.resolve(path.fileName.toString() + "_${'$'}_merge_conflict_$i")
            if (!exists(newPath)) {
                newFiles.add(newPath)
                return newPath
            }
            i += 1
        }
    }
}
