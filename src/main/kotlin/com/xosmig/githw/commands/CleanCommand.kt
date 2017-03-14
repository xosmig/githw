package com.xosmig.githw.commands

import com.xosmig.githw.Exclude
import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.objects.GitFile
import com.xosmig.githw.refs.Head
import com.xosmig.githw.utils.isEmpty
import java.nio.file.Files
import java.nio.file.Path

fun gitClean(root: Path) {
//    val gitDir = root.resolve(GIT_DIR_PATH)
//    val tree = Head.load(gitDir).commit.rootTree
//    val exclude = Exclude.loadFromRoot(root)
//
//    fun impl(path: Path) {
//        val relative = root.relativize(path)
//        if (exclude.contains(relative)) {
//            return
//        }
//        if (Files.isDirectory(path)) {
//            for (next in Files.newDirectoryStream(path)) {
//                impl(next)
//            }
//            if (Files.newDirectoryStream(path).isEmpty()) {
//                Files.delete(path)
//            }
//        } else {
//            if (tree.resolve(path) !is GitFile) {
//                Files.delete(path)
//            }
//        }
//    }
//
//    impl(root)
    TODO("not implemented")
}
