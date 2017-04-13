package com.xosmig.githw.controller

import com.xosmig.githw.*
import com.xosmig.githw.index.Index
import com.xosmig.githw.objects.*
import com.xosmig.githw.objects.Commit.Companion.defaultAuthor
import com.xosmig.githw.refs.Head
import com.xosmig.githw.utils.Sha256
import java.nio.file.Path
import java.util.*

interface GithwController {

    val loadedBank: LoadedObjectsBank

    val root: Path
    val head: Head
    val commit: Commit
    val tree: GitTree
    val index: Index
    val ignore: Ignore
    val treeWithIndex: GitTree

    val gitDir: Path
        get() = root.resolve(GIT_DIR_PATH)

    val branchesDir: Path
        get() = gitDir.resolve(BRANCHES_PATH)

    val headPath: Path
        get() = gitDir.resolve(HEAD_PATH)

    val indexDir: Path
        get() = gitDir.resolve(INDEX_PATH)

    fun getLog(): List<Commit>

    fun detach(sha256: Sha256)

    /**
     * Record changes to the repository.
     *
     * @param[message] commit message
     * @param[author] the name of the author of the commit
     * @param[date] date of the commit
     */
    fun commit(message: String, date: Date = Date(), author: String = defaultAuthor())

    fun remove(path: Path)

    /**
     * Reset working tree files.
     *
     * @param[path] path to a directory or a file to reset.
     */
    fun reset(path: Path)

    /**
     * Reset all files.
     */
    fun resetAll() { reset(root) }

    /**
     * Switch the current branch to [branchName] and updated the working directory.
     *
     * @param[branchName] a branch to switch to.
     * @param[createIfAbsent] if this parameter is set and the required branch doesn't exist,
     * a new branch will be created
     */
    fun switchBranch(branchName: String, createIfAbsent: Boolean = false)

    /**
     * Delete the given branch.
     *
     * @param[branchName] a branch to delete.
     *
     * @throws[IllegalArgumentException] if [branchName] is a current branch or doesn't exist
     */
    fun deleteBranch(branchName: String)

    fun getUnstagedFiles(path: Path): List<Path>

    fun getUnstagedAndUpdatedFiles(path: Path): List<Path>

    /**
     * Remove all files which are not tracked nor ignored and empty directories.
     */
    fun clean(start: Path)

    fun cleanAll() { clean(root) }

    fun newBranch(branchName: String)

    fun getBranches(): List<String>

    fun add(path: Path)

    /**
     * Add all unstaged changes to index.
     */
    fun addAll() = add(root)

    fun addToIgnore(vararg patterns: String)

    fun merge( otherBranchName: String,
               message: String? = null,
               author: String? = defaultAuthor(),
               failOnConflict: Boolean = false ): List<Path>

    fun walkExclude(start: Path, childrenFirst: Boolean = false, onlyFiles: Boolean = false): List<Path>
}