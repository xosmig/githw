
package com.xosmig.githw.objects

import com.github.andrewoma.dexx.kollection.ImmutableList
import com.github.andrewoma.dexx.kollection.toImmutableList
import com.xosmig.githw.utils.Sha256
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Path
import java.util.*
import com.xosmig.githw.utils.checkContent

class Commit private constructor( gitDir: Path,
                                  val message: String,
                                  val parents: ImmutableList<GitObject>,
                                  val rootTree: GitTree,
                                  val date: Date,
                                  val author: String = defaultAuthor(),
                                  knownSha256: Sha256? ): GitObjectLoaded(gitDir, knownSha256 ) {

    companion object {
        @Throws(IOException::class)
        fun load(gitDir: Path, sha256: Sha256, ins: ObjectInputStream): Commit {
            val msg = ins.readObject() as String
            val parents = (ins.readObject() as List<*>)
                    .checkContent(Sha256::class)
                    .map { GitObjectFromDisk.create(gitDir, it) }
                    .toImmutableList()
            val rootTree = GitObject.load(gitDir, ins.readObject() as Sha256) as GitTree
            val date = ins.readObject() as Date
            val author = ins.readObject() as String

            return Commit(gitDir, msg, parents, rootTree, date, author, sha256)
        }

        @Throws(IOException::class)
        fun loadFromHead(gitDir: Path, ins: ObjectInputStream): Commit {
            return GitObject.load(gitDir, ins.readObject() as Sha256) as Commit
        }

        fun create(gitDir: Path, message: String, parents: List<GitObject>, rootTree: GitTree,
                   date: Date = Date(), author: String = defaultAuthor()): Commit {
            return Commit(gitDir, message, parents.toImmutableList(), rootTree, date, author, knownSha256 = null)
        }

        fun defaultAuthor(): String = System.getProperty("user.name")
    }

    fun copy( message: String = this.message,
              parents: ImmutableList<GitObject> = this.parents,
              rootTree: GitTree = this.rootTree,
              date: Date = this.date,
              author: String = this.author ) = create(gitDir, message, parents, rootTree, date, author)

    override fun writeToDiskImpl() {
        super.writeToDiskImpl()
        rootTree.writeToDisk()
    }

    @Throws(IOException::class)
    override fun writeContentTo(out: ObjectOutputStream) {
        out.writeObject(message)
        out.writeObject(parents.map { it.sha256 })
        out.writeObject(rootTree.sha256)
        out.writeObject(date)
        out.writeObject(author)
    }

    @Throws(IOException::class)
    fun writeToHead(ins: ObjectOutputStream) = ins.writeObject(sha256)
}


