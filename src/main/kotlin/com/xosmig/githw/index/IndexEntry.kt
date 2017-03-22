package com.xosmig.githw.index

import com.xosmig.githw.INDEX_PATH
import com.xosmig.githw.controller.GithwController
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files.*
import java.nio.file.Path
import java.nio.file.Paths

abstract class IndexEntry private constructor(protected val githw: GithwController, val pathToFile: Path) {

    companion object {
        fun load(githw: GithwController, pathToEntryFile: Path): IndexEntry {
            newInputStream(pathToEntryFile).use {
                ObjectInputStream(it).use {
                    val type = it.readObject()
                    val pathToFile = Paths.get(it.readObject() as String)
                    return when (type) {
                        EditFile::class.java.name -> EditFile(githw, pathToFile, it.readObject() as ByteArray)
                        RemoveFile::class.java.name -> RemoveFile(githw, pathToFile)
                        else -> throw IllegalStateException("Unsupported IndexEntry type: '$type'")
                    }
                }
            }
        }
    }

    fun writeToDisk() {
        val last: Int = newDirectoryStream(githw.indexDir)
                .map { it.fileName.toString().toInt() }
                .max() ?: 0
        val name = githw.indexDir.resolve((last + 1).toString())
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

    class EditFile(githw: GithwController, pathToFile: Path, val content: ByteArray): IndexEntry(githw, pathToFile) {
        override fun writeContentTo(out: ObjectOutputStream) {
            super.writeContentTo(out)
            out.writeObject(content)
        }

        override fun toString(): String = "$pathToFile    (modified / new)"
    }

    class RemoveFile(githw: GithwController, pathToFile: Path): IndexEntry(githw, pathToFile) {
        override fun toString(): String = "$pathToFile    (removed)"
    }
}
