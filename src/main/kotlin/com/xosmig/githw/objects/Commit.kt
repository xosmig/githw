
package com.xosmig.githw.objects

import com.xosmig.githw.utils.Sha256
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Path
import java.util.*

class Commit private constructor( gitDir: Path,
                                  val message: String,
                                  val previousCommit: Sha256?,
                                  val rootTree: GitTree,
                                  val date: Date,
                                  val author: String = defaultAuthor(),
                                  onDisk: Boolean ): GitObjectLoaded(gitDir, onDisk) {

    companion object {
        @Throws(IOException::class)
        fun load(gitDir: Path, ins: ObjectInputStream): Commit {
            val msg = ins.readObject() as String
            val prevCommit = ins.readObject() as Sha256?
            val rootTree = GitObject.load(gitDir, ins.readObject() as Sha256) as GitTree
            val date = ins.readObject() as Date
            val author = ins.readObject() as String

            return Commit(gitDir, msg, prevCommit, rootTree, date, author, onDisk = true)
        }

        @Throws(IOException::class)
        fun loadFromHead(gitDir: Path, ins: ObjectInputStream): Commit {
            return GitObject.load(gitDir, ins.readObject() as Sha256) as Commit
        }

        fun create(gitDir: Path, message: String, previousCommit: Sha256?, rootTree: GitTree,
                   date: Date = Date(), author: String = defaultAuthor()): Commit {
            return Commit(gitDir, message, previousCommit, rootTree, date, author, onDisk = false)
        }

        fun defaultAuthor(): String = System.getProperty("user.name")
    }

    override fun writeToDiskImpl() {
        super.writeToDiskImpl()
        rootTree.writeToDisk()
    }

    @Throws(IOException::class)
    override fun writeContentTo(out: ObjectOutputStream) {
        out.writeObject(message)
        out.writeObject(previousCommit)
        out.writeObject(rootTree.sha256)
        out.writeObject(date)
        out.writeObject(author)
    }

    @Throws(IOException::class)
    fun writeToHead(ins: ObjectOutputStream) = ins.writeObject(sha256)
}


