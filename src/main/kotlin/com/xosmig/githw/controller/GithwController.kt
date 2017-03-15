package com.xosmig.githw.controller

import com.xosmig.githw.*
import com.xosmig.githw.index.Index
import com.xosmig.githw.index.IndexEntry
import com.xosmig.githw.objects.Commit
import com.xosmig.githw.objects.GitFSObject
import com.xosmig.githw.objects.GitTree
import com.xosmig.githw.refs.Branch
import com.xosmig.githw.refs.Head
import com.xosmig.githw.utils.FilesUtils
import java.nio.file.Files.*
import java.nio.file.Path
import java.util.*

class GithwController(var root: Path) {

    /**
     * Create an empty repository in the given directory.
     */
    fun init() {
        val state = GithwState(root)
        createDirectories(state.gitDir)

        createDirectories(state.gitDir.resolve(OBJECTS_PATH))
        createDirectories(state.gitDir.resolve(BRANCHES_PATH))
        createDirectories(state.gitDir.resolve(TAGS_PATH))
        createDirectories(state.gitDir.resolve(INDEX_PATH))

        createFile(state.gitDir.resolve(HEAD_PATH))
        createFile(state.gitDir.resolve(EXCLUDE_PATH))

        val commit = Commit.create(state.gitDir,
                message = "Initial commit",
                previousCommit = null,
                rootTree = GitTree.create(state.gitDir, emptyMap()),
                date = Date()
        )
        val branch = Branch(state.gitDir, "master", commit)
        branch.writeToDisk()
        Head.BranchPointer(state.gitDir, branch).writeToDisk()
    }

    /**
     * Record changes to the repository.
     *
     * @param[root] path to the project root directory
     * @param[message] commit message
     * @param[author] the name of the author of the commit
     * @param[date] date of the commit
     */
    fun commit(message: String, date: Date = Date(), author: String = Commit.defaultAuthor()) {
        val state = GithwState(root)

        val newCommit = Commit.create(state.gitDir, message, state.commit.sha256, state.treeWithIndex, date, author)
        Index.clear(state.gitDir)
        newCommit.writeToDisk()

        val head = state.head
        when (head) {
            is Head.BranchPointer -> head.branch.copy(commit = newCommit).writeToDisk()
            is Head.CommitPointer -> head.copy(commit = newCommit).writeToDisk()
            else -> throw IllegalStateException("Unknown head type: '${head.javaClass.name}'")
        }
    }

    fun remove(path: Path) {
        if (!exists(path)) {
            throw IllegalArgumentException("Invalid path '$path'")
        }
        val state = GithwState(root)

        for (current in walkExclude(path, childrenFirst = true, onlyFiles = false)) {
            if (isRegularFile(current)) {
                IndexEntry.RemoveFile(state.gitDir, root.relativize(current)).writeToDisk()
            }
            if (isRegularFile(current) || FilesUtils.isEmptyDir(current)) {
                delete(current)
            }
        }
    }

    /**
     * Restore working tree files.
     *
     * @param[root] path to the working directory.
     * @param[path] path to a directory or a file to restore.
     */
    fun revert(path: Path) {
        val state = GithwState(root)
        val obj = state.tree.resolve(root.relativize(path))?.loaded
                ?: throw IllegalArgumentException("file '$path' is not tracked")
        if (obj !is GitFSObject) {
            throw IllegalArgumentException("Bad object type to revert: '${obj.javaClass.name}'")
        }
        obj.revert(path)
    }

    fun switchBranch(branchName: String) {
        val state = GithwState(root)
        if (Index.load(state.gitDir).isNotEmpty()) {
            throw IllegalArgumentException("Index is not empty")
        }
        Head.BranchPointer(state.gitDir, Branch.load(state.gitDir, branchName)).writeToDisk()
        revert(root)
    }

    fun getUntrackedFiles(path: Path): List<Path> {
        val state = GithwState(root)
        return walkExclude(path, childrenFirst = true, onlyFiles = false)
                .filter { (isRegularFile(it) && !state.treeWithIndex.containsFile(root.relativize(it)))
                        || FilesUtils.isEmptyDir(it) }
    }

    /**
     * Remove all files which are not tracked nor ignored.
     */
    fun clean(path: Path) {
        getUntrackedFiles(path).forEach(::delete)
    }

    fun branchExist(branchName: String): Boolean {
        val state = GithwState(root)
        return exists(state.gitDir.resolve(BRANCHES_PATH).resolve(branchName))
    }

    fun newBranch(branchName: String) {
        val state = GithwState(root)
        if (branchExist(branchName)) {
            throw IllegalArgumentException("A branch named '$branchName' already exists.")
        }
        Branch(state.gitDir, branchName, state.commit).writeToDisk()
    }

    fun getBranches(): List<String> {
        val state = GithwState(root)
        val branchesDir = state.gitDir.resolve(BRANCHES_PATH)
        return newDirectoryStream(branchesDir).map { it.fileName.toString() }
    }

    fun add(path: Path) {
        val state = GithwState(root)
        for (file in walkExclude(path, onlyFiles = true)) {
            IndexEntry.EditFile(state.gitDir, root.relativize(file), readAllBytes(file)).writeToDisk()
        }
    }

    fun addExclude(vararg patterns: String) {
        Exclude.addToRoot(root, *patterns)
    }

    fun getExclude() = Exclude.loadFromRoot(root)

    fun walkExclude( path: Path,
                     childrenFirst: Boolean = false,
                     onlyFiles: Boolean = false,
                     exclude: Exclude = Exclude.loadFromRoot(root) ): List<Path> {

        val res = ArrayList<Path>()

        fun impl(current: Path) {
            if (exclude.contains(root.relativize(current))) {
                return
            }
            if (isDirectory(current)) {
                if (!childrenFirst && !onlyFiles) {
                    res.add(current)
                }
                for (next in newDirectoryStream(current)) {
                    impl(next)
                }
                if (childrenFirst && !onlyFiles) {
                    res.add(current)
                }
            } else {
                res.add(current)
            }
        }

        impl(path)
        return res
    }
}
