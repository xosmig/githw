package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.index.Index
import com.xosmig.githw.objects.Commit
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
fun commit(root: Path, message: String, author: String = getDefaultAuthor(), date: Date = Date()) {
    val gitDir = root.resolve(GIT_DIR_PATH)
    val index = Index.load(gitDir)
    val previous = Commit.loadFromHead(gitDir)
    val commit = Commit(gitDir, message, previous.getSha256(), previous.rootTree, date, author)
    index.applyToTree(commit.rootTree)
    commit.writeToDisk()
    Index.clear(gitDir)
}

private fun getDefaultAuthor(): String = System.getProperty("user.primaryName")
