package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.objects.GitFile
import com.xosmig.githw.objects.GitObject
import com.xosmig.githw.objects.GitObjectLoaded
import com.xosmig.githw.objects.GitTree
import com.xosmig.githw.refs.Head
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * Restore working tree files.
 *
 * @param[root] path to the working directory.
 * @param[path] path to a directory or a file to restore.
 */
@Throws(IOException::class)
fun checkout(root: Path, path: Path) {
    val gitDir = root.resolve(GIT_DIR_PATH)
    val obj = Head.load(gitDir).getLastCommit().rootTree.resolve(root.relativize(path))?.loaded
            ?: throw IllegalArgumentException("Path '$path' not found.")
    checkoutImpl(path, obj)
}

@Throws(IOException::class)
private fun checkoutImpl(path: Path, obj: GitObjectLoaded) {
    when (obj) {
        is GitFile -> {
            Files.createDirectories(path.parent)
            Files.newOutputStream(path).use {
                it.write(obj.content)
            }
        }
        is GitTree -> for ((name, child) in obj.children) {
            checkoutImpl(path.resolve(name), child.loaded)
        }
        else -> throw IllegalStateException("Unknown child type in `GitTree`: '${obj.javaClass.name}'")
    }
}
