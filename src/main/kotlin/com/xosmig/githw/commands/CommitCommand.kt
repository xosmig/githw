package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.index.Index
import com.xosmig.githw.objects.Commit
import com.xosmig.githw.refs.Head
import java.io.IOException
import java.nio.file.Path
import java.util.*

/**
 * Record changes to the repository.
 *
 * @param[root] path to the project root directory
 * @param[message] commit message
 * @param[author] the name of the author of the commit
 * @param[date] date of the commit
 */
@Throws(IOException::class)
fun commit(root: Path, message: String, author: String = Commit.defaultAuthor(), date: Date = Date()) {
    val gitDir = root.resolve(GIT_DIR_PATH)
    val index = Index.load(gitDir)
    val head = Head.load(gitDir)
    val previous = head.getLastCommit()
    val commit = Commit(gitDir, message, previous.getSha256(), previous.rootTree, date, author)

    index.applyToTree(commit.rootTree)
    commit.writeToDisk()
    Index.clear(gitDir)

    when (head) {
        is Head.BranchPointer -> head.branch.copy(commit = commit).writeToDisk()
        is Head.CommitPointer -> head.copy(commit = commit).writeToDisk()
        else -> throw IllegalStateException("Unknown head type: '${head.javaClass.name}'")
    }
}
