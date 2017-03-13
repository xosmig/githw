package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.index.Index
import com.xosmig.githw.refs.Branch
import com.xosmig.githw.refs.Head
import java.nio.file.Path

fun switchBranch(root: Path, branchName: String) {
    // TODO
    throw UnsupportedOperationException("not implemented")
//    val gitDir = root.resolve(GIT_DIR_PATH)
//    if (Index.load(gitDir).isNotEmpty()) {
//        throw IllegalArgumentException("Index is not empty")
//    }
//    val head = Head.load(gitDir)
//    if (head is Head.BranchPointer && branchName == head.branch.name) {
//        return
//    }
//    Branch.load(gitDir, branchName).commit.rootTree
//            .switchFrom(dst = root, previous = head.getLastCommit().rootTree)
}
