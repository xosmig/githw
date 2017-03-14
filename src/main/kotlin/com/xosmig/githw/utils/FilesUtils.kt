package com.xosmig.githw.utils

import com.xosmig.githw.Exclude
import java.nio.file.Files.*
import java.nio.file.Path
import java.util.*

object FilesUtils {
    fun deleteExclude(path: Path, root: Path) {
        deleteExclude(path, Exclude.loadFromRoot(root))
    }

    fun deleteExclude(path: Path, exclude: Exclude) {
        if (!exists(path) || exclude.contains(path)) {
            return
        }
        if (isDirectory(path)) {
            for (child in newDirectoryStream(path)) {
                deleteExclude(child, exclude)
            }
        }
        delete(path)
    }

    fun walkExclude( root: Path, path: Path,
                     childrenFirst: Boolean = false,
                     onlyFiles: Boolean = false,
                     exclude: Exclude = Exclude.loadFromRoot(root) ): List<Path> {

        val res = ArrayList<Path>()

        fun impl(current: Path) {
            if (exclude.contains(root.relativize(current))) {
                return
            }
            if (isDirectory(current)) {
                if (!childrenFirst && !onlyFiles) {
                    res.add(current)
                }
                for (next in newDirectoryStream(current)) {
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

    fun copyRecursive(source: Path, target: Path) {
        for (path in walk(source)) {
            val rel = source.relativize(path)
            if (isRegularFile(path)) {
                if (rel.parent != null) {
                    createDirectories(target.resolve(rel.parent))
                }
                copy(path, target.resolve(rel))
            } else {
                createDirectories(target.resolve(rel))
            }
        }
    }

    fun isEmptyDir(path: Path) = isDirectory(path) && newDirectoryStream(path).isEmpty()
}
