package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.objects.GitFSObject
import com.xosmig.githw.refs.Head
import java.io.IOException
import java.nio.file.Path

/**
 * Restore working tree files.
 *
 * @param[root] path to the working directory.
 * @param[path] path to a directory or a file to restore.
 */
@Throws(IOException::class)
fun revert(root: Path, path: Path) {
    val gitDir = root.resolve(GIT_DIR_PATH)
    val obj = (Head.load(gitDir).commit.rootTree.resolve(root.relativize(path))?.loaded
            ?: throw IllegalArgumentException("file '$path' is not tracked"))
    if (obj !is GitFSObject) {
        throw IllegalArgumentException("Bad object type to revert: '${obj.javaClass.name}'")
    }
    obj.revert(path)
}
