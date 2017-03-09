package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.index.Index
import com.xosmig.githw.objects.Commit
import java.io.IOException
import java.nio.file.Path
import java.util.*

/**
 * Record changes to the repository.
 */
@Throws(IOException::class)
fun commit(root: Path, message: String, date: Date, author: String) {
    val gitDir = root.resolve(GIT_DIR_PATH)
    val index = Index.load(gitDir)
    val previous = Commit.loadFromHead(gitDir)
    val commit = Commit(gitDir, message, previous.getSha256(), previous.rootTree, date, author)
    index.applyToTree(commit.rootTree)
    commit.writeToDisk()
    Index.clear(gitDir)
}

/**
 * Record changes to the repository with current date and default author.
 */
@Throws(IOException::class)
fun commit(root: Path, message: String) {
    // TODO: Commit with author from git settings
    commit(root, message, Date(), "todo_author_from_settings")
}

