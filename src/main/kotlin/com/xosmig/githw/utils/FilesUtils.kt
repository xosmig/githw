package com.xosmig.githw.utils

import com.xosmig.githw.Exclude
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

object FilesUtils {
    /*fun deleteRecursive(path: Path) {
        if (!Files.exists(path)) {
            return
        }
        if (Files.isDirectory(path)) {
            for (child in Files.newDirectoryStream(path)) {
                deleteRecursive(child)
            }
        }
        Files.delete(path)
    }*/

    fun walkExclude(root: Path, path: Path, childrenFirst: Boolean = false, onlyFiles: Boolean = false): List<Path> {
        val exclude = Exclude.loadFromRoot(root)
        val res = ArrayList<Path>()

        fun impl(current: Path) {
            if (exclude.contains(root.relativize(current))) {
                return
            }
            if (Files.isDirectory(current)) {
                if (!childrenFirst && !onlyFiles) {
                    res.add(current)
                }
                for (next in Files.newDirectoryStream(current)) {
                    impl(next)
                }
                if (childrenFirst && !onlyFiles) {
                    res.add(current)
                }
            } else {
                res.add(current)
            }
        }

        impl(path)
        return res
    }

    fun isEmptyDir(path: Path) = Files.isDirectory(path) && Files.newDirectoryStream(path).isEmpty()
}
