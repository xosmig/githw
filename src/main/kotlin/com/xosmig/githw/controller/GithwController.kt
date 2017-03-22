package com.xosmig.githw.controller

import com.xosmig.githw.*
import com.xosmig.githw.index.Index
import com.xosmig.githw.objects.*
import com.xosmig.githw.refs.Head
import java.nio.file.Path

interface GithwController {

    val loadedBank: LoadedObjectsBank

    val root: Path
    val head: Head
    val commit: Commit
    val tree: GitTree
    val index: Index
    val ignore: Ignore
    val treeWithIndex: GitTree

    val gitDir: Path
        get() = root.resolve(GIT_DIR_PATH)

    val branchesDir: Path
        get() = gitDir.resolve(BRANCHES_PATH)

    val headPath: Path
        get() = gitDir.resolve(HEAD_PATH)

    val indexDir: Path
        get() = gitDir.resolve(INDEX_PATH)
}