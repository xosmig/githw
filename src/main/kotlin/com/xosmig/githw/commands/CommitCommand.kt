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
fun commit(root: Path, message: String, date: Date = Date(), author: String = Commit.defaultAuthor()) {
    val gitDir = root.resolve(GIT_DIR_PATH)
    val index = Index.load(gitDir)
    val head = Head.load(gitDir)
    val previous = head.commit
    val rootTree = index.applyToTree(previous.rootTree)
    Index.clear(gitDir)
    val commit = Commit.create(gitDir, message, previous.sha256, rootTree, date, author)
    commit.writeToDisk()

    when (head) {
        is Head.BranchPointer -> head.branch.copy(commit = commit).writeToDisk()
        is Head.CommitPointer -> head.copy(commit = commit).writeToDisk()
        else -> throw IllegalStateException("Unknown head type: '${head.javaClass.name}'")
    }
}
