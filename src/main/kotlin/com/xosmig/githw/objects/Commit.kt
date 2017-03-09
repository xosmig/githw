
package com.xosmig.githw.objects

import com.xosmig.githw.HEAD_PATH
import com.xosmig.githw.refs.Branch
import com.xosmig.githw.refs.Tag
import com.xosmig.githw.utils.Sha256
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class Commit(gitDir: Path,
             val message: String,
             val previousCommit: Sha256?,
             val rootTree: GitTree,
             val date: Date,
             val author: String): GitObjectLoaded(gitDir) {

    companion object {
        @Throws(IOException::class)
        fun load(gitDir: Path, ins: ObjectInputStream): Commit {
            val msg = ins.readObject() as String
            val prevCommit = ins.readObject() as Sha256?
            val rootTree = GitObjectFromDisk(gitDir, ins.readObject() as Sha256).loaded as GitTree
            val date = ins.readObject() as Date
            val author = ins.readObject() as String

            return Commit(gitDir, msg, prevCommit, rootTree, date, author)
        }

        @Throws(IOException::class)
        fun loadFromHead(gitDir: Path): Commit {
            Files.newInputStream(gitDir.resolve(HEAD_PATH)).use {
                ObjectInputStream(it).use {
                    val type = it.readObject() as String
                    return when (type) {
                        Branch::class.java.name -> Branch.load(gitDir, it.readObject() as String).commit
                        Tag::class.java.name -> Tag.load(gitDir, it.readObject() as String).commit
                        Commit::class.java.name -> GitObjectFromDisk(gitDir, it.readObject() as Sha256)
                        else -> throw IllegalStateException("Unknown head type: '$type'")
                    }.loaded as Commit
                }
            }
        }
    }

    @Throws(IOException::class)
    override fun writeContentTo(out: ObjectOutputStream) {
        out.writeObject(message)
        out.writeObject(previousCommit)
        out.writeObject(rootTree.getSha256())
        out.writeObject(date)
        out.writeObject(author)
    }
}


