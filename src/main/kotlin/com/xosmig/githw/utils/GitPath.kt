/*
package com.xosmig.githw.utils

import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

class GitPath private constructor(private val root: Path, private val path: Path, private val relative: Path) {

    companion object {
        fun create(root: String, path: String, fs: FileSystem = FileSystems.getDefault()): GitPath {
            val fsRoot = fs.getPath(root).normalize()
            val fsPath = fs.getPath(path).normalize()
            return GitPath(fsRoot, fsPath, fsRoot.relativize(fsPath))
        }
    }

    fun resolve(other: String): GitPath {
        return GitPath(root, path.resolve(other), relative.resolve(other))
    }

    val outputStream
        get() = Files.newOutputStream(path)

    val inputStream
        get() = Files.newInputStream(path)

    val parent
        get() = GitPath()

    fun createFile() {
        Files.createDirectories(path.parent)
        Files.createFile(path)
    }

    fun createDir() = Files.createDirectories(path)
}
*/
