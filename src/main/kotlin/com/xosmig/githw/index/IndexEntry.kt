package com.xosmig.githw.index

import com.xosmig.githw.INDEX_PATH
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files.*
import java.nio.file.Path
import java.nio.file.Paths

abstract class IndexEntry private constructor(protected val gitDir: Path, val pathToFile: Path) {

    companion object {
        @Throws(IOException::class)
        fun load(gitDir: Path, pathToEntryFile: Path): IndexEntry {
            newInputStream(pathToEntryFile).use {
                ObjectInputStream(it).use {
                    val type = it.readObject()
                    val pathToFile = Paths.get(it.readObject() as String)
                    return when (type) {
                        EditFile::class.java.name -> EditFile(gitDir, pathToFile, it.readObject() as ByteArray)
                        RemoveFile::class.java.name -> RemoveFile(gitDir, pathToFile)
                        else -> throw IllegalStateException("Unsupported IndexEntry type: '$type'")
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    fun writeToDisk() {
        val indexDir = gitDir.resolve(INDEX_PATH)
        val last: Int = newDirectoryStream(indexDir)
                .map { it.fileName.toString().toInt() }
                .max() ?: 0
        val name = indexDir.resolve((last + 1).toString())
        newOutputStream(name).use {
            ObjectOutputStream(it).use {
                it.writeObject(javaClass.name)
                writeContentTo(it)
            }
        }
    }

    open fun writeContentTo(out: ObjectOutputStream) {
        out.writeObject(pathToFile.toString())
    }

    class EditFile(gitDir: Path, pathToFile: Path, val content: ByteArray): IndexEntry(gitDir, pathToFile) {
        override fun writeContentTo(out: ObjectOutputStream) {
            super.writeContentTo(out)
            out.writeObject(content)
        }
    }

    class RemoveFile(gitDir: Path, pathToFile: Path): IndexEntry(gitDir, pathToFile)
}
