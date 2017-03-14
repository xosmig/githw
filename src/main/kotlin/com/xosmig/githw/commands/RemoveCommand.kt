package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.index.IndexEntry
import com.xosmig.githw.utils.FilesUtils
import java.io.IOException
import java.nio.file.Files.*
import java.nio.file.Path

@Throws(IOException::class)
fun remove(root: Path, path: Path) {
    if (!exists(path)) {
        throw IllegalArgumentException("Invalid path '$path'")
    }
    val gitDir = root.resolve(GIT_DIR_PATH)

    for (current in FilesUtils.walkExclude(root, path, childrenFirst = true, onlyFiles = false)) {
        if (isRegularFile(current)) {
            IndexEntry.RemoveFile(gitDir, root.relativize(current)).writeToDisk()
        }
        if (isRegularFile(current) || FilesUtils.isEmptyDir(current)) {
            delete(current)
        }
    }
}
