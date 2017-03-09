
package com.xosmig.githw.objects

import com.xosmig.githw.utils.Sha256
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Path
import java.util.*

class Commit(gitDir: Path,
             val message: String,
             val previousCommit: GitObject,
             val rootTree: GitObject,
             val date: Date,
             val author: String): GitObjectLoaded(gitDir) {

    companion object {
        @Throws(IOException::class)
        fun load(gitDir: Path, ins: ObjectInputStream): Commit {
            val msg = ins.readObject() as String
            val prevCommit = GitObjectFromDisk(gitDir, ins.readObject() as Sha256)
            val rootTree = GitObjectFromDisk(gitDir, ins.readObject() as Sha256)
            val date = ins.readObject() as Date
            val author = ins.readObject() as String
            return Commit(gitDir, msg, prevCommit, rootTree, date, author)
        }
    }

    @Throws(IOException::class)
    override fun writeContentTo(out: ObjectOutputStream) {
        out.writeObject(message)
        out.writeObject(previousCommit.getSha256())
        out.writeObject(rootTree.getSha256())
        out.writeObject(date)
        out.writeObject(author)
    }
}


/*
@Throws(IOException::class)
fun commit(root: Path, message: String, date: Date, author: String) {
    val gitDir = root.resolve(GITHW_DIR)

    val index = Index.load(gitDir)
    val head = Head.load(gitDir)

    val previous = head.getCommit()
    val tree = previous.root.loaded as GitTree

    for (entry in index.entries) {
        when (entry) {
            is IndexEditFile -> tree.putFile(entry.path, GitFile(gitDir, entry.content))
            is IndexRemoveFile -> tree.removeFile(entry.path)
            else -> throw UnsupportedOperationException("Unknown IndexEntry type")
        }
    }

    tree.writeToDisk()
    Commit(message, previous, tree, date, author).writeToDisk(gitDir.resolve(COMMITS_PATH))
}

@Throws(IOException::class)
fun commit(root: Path, message: String) {
    // TODO: Commit with author from git settings
    commit(root, message, Date(), "todo_author_from_settings")
}
*/
