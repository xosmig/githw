package com.xosmig.githw.index

import com.xosmig.githw.INDEX_PATH
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.nio.file.Files
import java.nio.file.Path

abstract class IndexEntry private constructor(gitDir: Path, val pathToFile: Path): Serializable {
    protected @Transient var gitDir: Path = gitDir
        private set

    companion object {
        @Throws(IOException::class)
        fun load(gitDir: Path, pathToEntryFile: Path): IndexEntry {
            Files.newInputStream(pathToEntryFile).use {
                ObjectInputStream(it).use {
                    val res = it.readObject() as IndexEntry
                    res.gitDir = gitDir
                    return res
                }
            }
        }
    }

    @Throws(IOException::class)
    fun writeToDisk() {
        val indexDir = gitDir.resolve(INDEX_PATH)
        val last: Int = Files.newDirectoryStream(indexDir)
                .map { it.fileName.toString().toInt() }
                .max() ?: 0
        val name = indexDir.resolve((last + 1).toString())
        Files.newOutputStream(name).use {
            ObjectOutputStream(it).use {
                it.writeObject(this)
            }
        }
    }

    class IndexEditFile(gitDir: Path, pathToFile: Path, val content: ByteArray): IndexEntry(gitDir, pathToFile)
    class IndexRemoveFile(gitDir: Path, pathToFile: Path): IndexEntry(gitDir, pathToFile)
}
