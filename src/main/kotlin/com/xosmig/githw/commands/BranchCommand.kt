package com.xosmig.githw.commands

import com.xosmig.githw.BRANCHES_PATH
import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.refs.Branch
import com.xosmig.githw.refs.Head
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

@Throws(IOException::class)
fun getBranches(root: Path): List<String> {
    val gitDir = root.resolve(GIT_DIR_PATH)
    val branchesDir = gitDir.resolve(BRANCHES_PATH)
    return Files.newDirectoryStream(branchesDir).map { it.fileName.toString() }
}

@Throws(IOException::class)
fun newBranch(root: Path, branchName: String) {
    if (branchExist(root, branchName)) {
        throw IllegalArgumentException("A branch named '$branchName' already exists.")
    } else {
        val gitDir = root.resolve(GIT_DIR_PATH)
        val commit = Head.load(gitDir).getLastCommit()
        Branch(gitDir, branchName, commit).writeToDisk()
    }
}

@Throws(IOException::class)
fun branchExist(root: Path, branchName: String): Boolean {
    return Files.exists(root.resolve(GIT_DIR_PATH).resolve(BRANCHES_PATH).resolve(branchName))
}
