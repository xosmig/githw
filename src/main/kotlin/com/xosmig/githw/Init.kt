package com.xosmig.githw

import com.xosmig.githw.objects.Commit
import com.xosmig.githw.objects.GitTree
import com.xosmig.githw.refs.Branch
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

/**
 * Create an empty repository in the given directory.
 */
@Throws(IOException::class)
fun init(root: Path) {
    val gitDir = root.resolve(GITHW_DIR)
    Files.createDirectories(gitDir)

    Files.createDirectories(gitDir.resolve(OBJECTS_PATH))
    Files.createDirectories(gitDir.resolve(BRANCHES_PATH))
    Files.createDirectories(gitDir.resolve(TAGS_PATH))

    Files.createFile(gitDir.resolve(HEAD_PATH))
    Files.createFile(gitDir.resolve(INDEX_PATH))

    val commit = Commit(gitDir, "Initial commit", null, GitTree(gitDir, emptyMap()), Date(), "")
    val branch = Branch(gitDir, "master", commit)
    branch.writeToDisk()
}
