package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.index.Index
import com.xosmig.githw.objects.GitFile
import com.xosmig.githw.refs.Head
import com.xosmig.githw.utils.FilesUtils.isEmptyDir
import com.xosmig.githw.utils.FilesUtils.walkExclude
import java.nio.file.Files.*
import java.nio.file.Path

fun getUntrackedFiles(root: Path, path: Path): List<Path> {
    val gitDir = root.resolve(GIT_DIR_PATH)
    val tree = Index.load(gitDir).applyToTree(Head.load(gitDir).commit.rootTree)

    return walkExclude(root, path, childrenFirst = true, onlyFiles = false)
            .filter { (isRegularFile(it) && tree.resolve(root.relativize(it))?.loaded !is GitFile) || isEmptyDir(it) }
}
