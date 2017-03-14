package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.index.Index
import com.xosmig.githw.refs.Branch
import com.xosmig.githw.refs.Head
import java.nio.file.Path

fun switchBranch(root: Path, branchName: String) {
    val gitDir = root.resolve(GIT_DIR_PATH)
    if (Index.load(gitDir).isNotEmpty()) {
        throw IllegalArgumentException("Index is not empty")
    }
    Head.BranchPointer(gitDir, Branch.load(gitDir, branchName)).writeToDisk()
    revert(root, root)
}
