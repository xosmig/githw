package com.xosmig.githw.controller

import com.xosmig.githw.Exclude
import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.index.Index
import com.xosmig.githw.refs.Head
import java.nio.file.Path

class GithwState(val root: Path) {
    val gitDir = root.resolve(GIT_DIR_PATH)!!
    val head by lazy { Head.load(gitDir) }
    val commit by lazy { head.commit }
    val tree by lazy { commit.rootTree }
    val index by lazy { Index.load(gitDir) }
    val exclude by lazy { Exclude.loadFromRoot(root) }
    val treeWithIndex by lazy { index.applyToTree(tree) }
}
