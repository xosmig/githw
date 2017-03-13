package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.index.Index
import java.nio.file.Path

fun switchBranch(root: Path, branchName: String) {
    val gitDir = root.resolve(GIT_DIR_PATH)
    if (Index.load(gitDir).isNotEmpty()) {
        throw IllegalArgumentException("Index is not empty")
    }
    throw UnsupportedOperationException("not implemented")
}
