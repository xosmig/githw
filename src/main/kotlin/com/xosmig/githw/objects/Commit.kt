
package com.xosmig.githw.objects

import com.github.andrewoma.dexx.kollection.ImmutableList
import com.github.andrewoma.dexx.kollection.toImmutableList
import com.xosmig.githw.controller.GithwController
import com.xosmig.githw.objects.GitObjectFromDisk.Companion.getObjectFromDisk
import com.xosmig.githw.objects.GitObjectFromDisk.Companion.loadObject
import com.xosmig.githw.utils.Sha256
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Path
import java.util.*
import com.xosmig.githw.utils.checkContent

class Commit private constructor( githw: GithwController,
                                  val message: String,
                                  val parents: ImmutableList<GitObject>,
                                  val rootTree: GitTree,
                                  val date: Date,
                                  val author: String = defaultAuthor(),
                                  knownSha256: Sha256? ): GitObjectLoaded(githw, knownSha256 ) {

    companion object {
        @Throws(IOException::class)
        internal fun load(githw: GithwController, sha256: Sha256, ins: ObjectInputStream): Commit {
            val msg = ins.readObject() as String
            val parents = (ins.readObject() as List<*>)
                    .checkContent(Sha256::class)
                    .map { githw.getObjectFromDisk(it) }
                    .toImmutableList()
            val rootTree = githw.loadObject(ins.readObject() as Sha256) as GitTree
            val date = ins.readObject() as Date
            val author = ins.readObject() as String

            return Commit(githw, msg, parents, rootTree, date, author, sha256)
        }

        @Throws(IOException::class)
        fun loadFromHead(githw: GithwController,ins: ObjectInputStream): Commit {
            return githw.loadObject(ins.readObject() as Sha256) as Commit
        }

        fun GithwController.createCommit(message: String, parents: List<GitObject>, rootTree: GitTree,
                   date: Date = Date(), author: String = defaultAuthor()): Commit {
            return Commit(this, message, parents.toImmutableList(), rootTree, date, author, knownSha256 = null)
        }

        fun defaultAuthor(): String = System.getProperty("user.name")
    }

    fun copy( message: String = this.message,
              parents: ImmutableList<GitObject> = this.parents,
              rootTree: GitTree = this.rootTree,
              date: Date = this.date,
              author: String = this.author ) = githw.createCommit(message, parents, rootTree, date, author)

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

    override fun toString(): String {
        val tab = '\t'
        return """
            |commit $sha256
            |Author:$tab$author
            |Date:$tab$date
            |
            |$tab$message
            |""".trimMargin()
    }
}


