package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.index.IndexEntry
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

@Throws(IOException::class)
fun add(root: Path, path: Path) {
    val gitDir = root.resolve(GIT_DIR_PATH)

    fun impl(path: Path) {
        if (path == gitDir) {
            return
        }
        if (Files.isDirectory(path)) {
            for (next in Files.newDirectoryStream(path)) {
                impl(next)
            }
        } else {
            IndexEntry.EditFile(gitDir, root.relativize(path), Files.readAllBytes(path)).writeToDisk()
        }
    }

    impl(path)
}
