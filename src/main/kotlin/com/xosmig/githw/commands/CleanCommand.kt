package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.objects.GitFile
import com.xosmig.githw.refs.Head
import com.xosmig.githw.utils.FilesUtils
import java.nio.file.Files.*
import java.nio.file.Path

/**
 * Remove all files which are not tracked nor ignored.
 */
fun gitClean(root: Path, path: Path) {
    val gitDir = root.resolve(GIT_DIR_PATH)
    val tree = Head.load(gitDir).commit.rootTree

    FilesUtils.walkExclude(root, path, childrenFirst = true, onlyFiles = false)
            .filter { (isRegularFile(it) && tree.resolve(it) !is GitFile) || FilesUtils.isEmptyDir(it) }
            .forEach { delete(it) }
}
