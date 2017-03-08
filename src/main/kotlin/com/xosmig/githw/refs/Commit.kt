package com.xosmig.githw.refs

import com.xosmig.githw.*
import com.xosmig.githw.objects.GitFile
import com.xosmig.githw.objects.GitTree
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class Commit(val message: String,
             val previous: Commit?,
             val root: GitTree,
             val date: Date,
             val author: String) {

    companion object {
        @Throws(IOException::class)
        fun readFromFile(path: Path): Commit {
            // TODO
            throw UnsupportedOperationException("not implemented")
        }
    }

    fun sha256(): String {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }

    @Throws(IOException::class)
    fun writeToFile(path: Path) {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }

    @Throws(IOException::class)
    fun writeToDisk(commitsDir: Path) {
        val sha256 = sha256()
        val dir = commitsDir.resolve(sha256.take(HASH_PREF_LENGTH))
        Files.createDirectory(dir)
        writeToFile(dir.resolve(sha256.drop(HASH_PREF_LENGTH)))
    }
}


/**
 * Record changes to the repository
 */
@Throws(IOException::class)
fun commit(root: Path, message: String, date: Date, author: String) {
    val gitDir = root.resolve(GITHW_DIR)
    val headPath = gitDir.resolve(HEAD_PATH)
    val indexPath = gitDir.resolve(INDEX_PATH)

    val index = Index.readFromFile(indexPath)
    val head = Head.readFromFile(headPath)

    val previous = head.getCommit()
    val tree = previous.root.load() as GitTree

    for (entry in index.entries) {
        when (entry) {
            is IndexEditFile -> tree.putFile(entry.path, GitFile(entry.content))
            is IndexRemoveFile -> tree.removeFile(entry.path)
            else -> throw UnsupportedOperationException("Unknown IndexEntry type")
        }
    }

    tree.writeToDisk(gitDir.resolve(OBJECTS_PATH))
    Commit(message, previous, tree, date, author).writeToDisk(gitDir.resolve(COMMITS_PATH))
}

/**
 * Record changes to the repository
 */
@Throws(IOException::class)
fun commit(root: Path, message: String) {
    // TODO: commit(root, message, current date, author from git settings)
    throw UnsupportedOperationException("not implemented")
}
