package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.index.IndexEntry
import com.xosmig.githw.utils.FilesUtils.isEmptyDir
import com.xosmig.githw.utils.FilesUtils.walkExclude
import java.io.IOException
import java.nio.file.Files.*
import java.nio.file.Path

@Throws(IOException::class)
fun remove(root: Path, path: Path) {
    if (!exists(path)) {
        throw IllegalArgumentException("Invalid path '$path'")
    }
    val gitDir = root.resolve(GIT_DIR_PATH)

    for (current in walkExclude(root, path, childrenFirst = true, onlyFiles = false)) {
        if (isRegularFile(current)) {
            IndexEntry.RemoveFile(gitDir, root.relativize(current)).writeToDisk()
        }
        if (isRegularFile(current) || isEmptyDir(current)) {
            delete(current)
        }
    }
}
