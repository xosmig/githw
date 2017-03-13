package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.index.IndexEntry
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

@Throws(IOException::class)
fun remove(root: Path, file: Path) {
    val gitDir = root.resolve(GIT_DIR_PATH)
    TODO("check that file is tracked")
    Files.deleteIfExists(file)
    IndexEntry.RemoveFile(gitDir, root.relativize(file)).writeToDisk()
}
