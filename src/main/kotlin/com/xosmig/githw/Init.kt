package com.xosmig.githw

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * Create an empty repository in the given directory.
 */
@Throws(IOException::class)
fun init(root: Path) {
    val gitDir = root.resolve(GITHW_DIR)
    Files.createDirectory(gitDir)

    Files.createDirectory(gitDir.resolve(OBJECTS_PATH))
    Files.createDirectory(gitDir.resolve(REFS_PATH))
    Files.createDirectory(gitDir.resolve(COMMITS_PATH))

    Files.createFile(gitDir.resolve(HEAD_PATH))
    Files.createFile(gitDir.resolve(INDEX_PATH))

    // TODO: create one initial commit and one branch
}
