package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.index.IndexEntry
import com.xosmig.githw.utils.FilesUtils
import java.io.IOException
import java.nio.file.Files.*
import java.nio.file.Path

@Throws(IOException::class)
fun add(root: Path, path: Path) {
    val gitDir = root.resolve(GIT_DIR_PATH)
    for (file in FilesUtils.walkExclude(root, path, onlyFiles = true)) {
        IndexEntry.EditFile(gitDir, root.relativize(file), readAllBytes(file)).writeToDisk()
    }
}
