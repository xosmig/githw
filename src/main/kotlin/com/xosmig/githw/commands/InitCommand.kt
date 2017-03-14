package com.xosmig.githw.commands

import com.xosmig.githw.*
import com.xosmig.githw.objects.Commit
import com.xosmig.githw.objects.GitTree
import com.xosmig.githw.refs.Branch
import com.xosmig.githw.refs.Head
import java.io.IOException
import java.nio.file.Files.*
import java.nio.file.Path
import java.util.*

/**
 * Create an empty repository in the given directory.
 */
@Throws(IOException::class)
fun init(root: Path) {
    val gitDir = root.resolve(GIT_DIR_PATH)
    createDirectories(gitDir)

    createDirectories(gitDir.resolve(OBJECTS_PATH))
    createDirectories(gitDir.resolve(BRANCHES_PATH))
    createDirectories(gitDir.resolve(TAGS_PATH))
    createDirectories(gitDir.resolve(INDEX_PATH))

    createFile(gitDir.resolve(HEAD_PATH))
    createFile(gitDir.resolve(EXCLUDE_PATH))

    val commit = Commit.create(gitDir, "Initial commit", null, GitTree.create(gitDir, emptyMap()), Date())
    val branch = Branch(gitDir, "master", commit)
    branch.writeToDisk()
    Head.BranchPointer(gitDir, branch).writeToDisk()
}
