package com.xosmig.githw.controller

import com.github.andrewoma.dexx.kollection.immutableListOf
import com.xosmig.githw.*
import com.xosmig.githw.index.Index
import com.xosmig.githw.index.IndexEntry
import com.xosmig.githw.objects.Commit
import com.xosmig.githw.objects.Commit.Companion.createCommit
import com.xosmig.githw.objects.Commit.Companion.defaultAuthor
import com.xosmig.githw.objects.GitFSObject
import com.xosmig.githw.objects.GitFile
import com.xosmig.githw.objects.GitTree.Companion.createEmptyTree
import com.xosmig.githw.refs.Branch
import com.xosmig.githw.refs.Branch.Companion.createBranch
import com.xosmig.githw.refs.Branch.Companion.loadBranch
import com.xosmig.githw.refs.Head
import com.xosmig.githw.utils.FilesUtils
import com.xosmig.githw.utils.FilesUtils.isEmptyDir
import com.xosmig.githw.utils.cache
import java.nio.file.Files.*
import java.nio.file.Path
import java.util.*

/**
 * Provides most common commands to work with a repository.
 */
class BasicGithwController(override var root: Path): GithwController {

    override val loadedCache: LoadedObjectsCache = LoadedObjectsCachePermanent()

    // independent caches: head, index, ignore

    override val gitDir = root.resolve(GIT_DIR_PATH)!!

    val headCache = cache { checkInitialized(); Head.load(this) }
    override val head by headCache

    val commitCache = cache(headCache) { head.commit }
    override val commit by commitCache

    val treeCache = cache(commitCache) { commit.rootTree }
    override val tree get() = commit.rootTree

    val indexCache = cache { checkInitialized(); Index.load(this) }
    override val index by indexCache

    val ignoreCache = cache { checkInitialized(); Ignore.loadFromRoot(root) }
    override val ignore by ignoreCache

    val treeWithIndexCache = cache(indexCache, treeCache) { index.applyToTree(tree) }
    override val treeWithIndex by treeWithIndexCache

    /**
     * Create an empty repository in [root].
     */
    @Synchronized
    fun init() {
        if (exists(gitDir)) {
            throw IllegalArgumentException("$gitDir already exists")
        }
        createDirectories(gitDir)

        createDirectories(gitDir.resolve(OBJECTS_PATH))
        createDirectories(gitDir.resolve(BRANCHES_PATH))
        createDirectories(gitDir.resolve(TAGS_PATH))
        createDirectories(gitDir.resolve(INDEX_PATH))

        createFile(gitDir.resolve(HEAD_PATH))
        createFile(gitDir.resolve(EXCLUDE_PATH))

        val commit = createCommit(
                message = "Initial commit",
                parents = emptyList(),
                rootTree = createEmptyTree(),
                date = Date()
        )
        val branch = createBranch("master", commit)
        branch.writeToDisk()
        writeToHead(branch)
    }

    fun getLog(): List<Commit> {
        val timeOut = IdentityHashMap<Commit, Int>()
        var time = 0

        fun Commit.dfs() {
            if (timeOut.containsKey(this)) { return }
            time += 1
            for (parent in parents) {
                (parent.loaded as Commit).dfs()
            }
            timeOut[this] = time
        }
        commit.dfs()

        return timeOut.toList().sortedBy { it.second }.map { it.first }
    }

    fun writeToHead(branch: Branch): Unit = Head.BranchPointer(this, branch).writeToDisk()

    fun writeToHead(commit: Commit): Unit = Head.CommitPointer(this, commit).writeToDisk()

    @Synchronized
    private fun commit(newCommit: Commit) {
        Index.clear(gitDir)
        indexCache.reset()
        newCommit.writeToDisk()

        val head = head
        when (head) {
            is Head.BranchPointer -> head.branch.copy(commit = newCommit).writeToDisk()
            is Head.CommitPointer -> head.copy(commit = newCommit).writeToDisk()
            else -> throw IllegalStateException("Unknown head type: '${head.javaClass.name}'")
        }

        headCache.reset()
    }

    /**
     * Record changes to the repository.
     *
     * @param[message] commit message
     * @param[author] the name of the author of the commit
     * @param[date] date of the commit
     */
    @Synchronized
    fun commit(message: String, date: Date = Date(), author: String = defaultAuthor()) {
        commit(createCommit(message, listOf(commit), treeWithIndex, date, author))
    }

    @Synchronized
    fun remove(path: Path) {
        if (!exists(path)) {
            throw IllegalArgumentException("Invalid path '$path'")
        }
        for (current in walkExclude(path, childrenFirst = true, onlyFiles = false)) {
            if (isRegularFile(current)) {
                IndexEntry.RemoveFile(this, root.relativize(current)).writeToDisk()
            }
            if (isRegularFile(current) || FilesUtils.isEmptyDir(current)) {
                delete(current)
            }
        }

        indexCache.reset()
    }

    /**
     * Restore working tree files.
     *
     * @param[path] path to a directory or a file to restore.
     */
    @Synchronized
    fun revert(path: Path) {
        val obj = tree.resolve(root.relativize(path))?.loaded
                ?: throw IllegalArgumentException("file '$path' is not tracked")
        if (obj !is GitFSObject) {
            throw IllegalArgumentException("Bad object type to refresh: '${obj.javaClass.name}'")
        }
        obj.revert(path)
    }

    /**
     * Switch the current branch to [branchName] and updated the working directory.
     *
     * @param[branchName] a branch to switch to.
     */
    @Synchronized
    fun switchBranch(branchName: String) {
        checkUpToDate()

        writeToHead(loadBranch(branchName))
        revert(root)

        headCache.reset()
    }

    /**
     * Delete the given branch.
     *
     * @param[branchName] a branch to delete.
     *
     * @throws[IllegalArgumentException] if [branchName] is a current branch or doesn't exist
     */
    @Synchronized
    fun deleteBranch(branchName: String) {
        val head = head
        if (head is Head.BranchPointer && head.branch.name == branchName) {
            throw IllegalArgumentException("Can not remove the current branch: '$branchName'")
        }
        val path = gitDir.resolve(BRANCHES_PATH).resolve(branchName)
        if (!exists(path)) {
            throw IllegalArgumentException("Branch '$branchName' doesn't exist")
        }
        delete(path)
    }

    @Synchronized
    fun getUntrackedAndUpdatedFiles(path: Path): List<Path> {
        return walkExclude(path, onlyFiles = true)
                .filter { isRegularFile(it) }
                .filter {
                    val gitObj = treeWithIndex.resolve(root.relativize(it))?.loaded
                    gitObj !is GitFile || gitObj.sha256 != FilesUtils.countSha256(path)
                }
    }

    @Synchronized
    fun getUntrackedFiles(path: Path): List<Path> {
        return walkExclude(path, onlyFiles = true)
                .filter { isRegularFile(it) && !treeWithIndex.containsFile(root.relativize(it)) }
    }

    /**
     * Remove all files which are not tracked nor ignored.
     */
    @Synchronized
    fun clean(path: Path) {
        getUntrackedFiles(path).forEach(::delete)
        walkExclude(path, childrenFirst = true, onlyFiles = false)
                .filter(::isEmptyDir)
                .forEach(::delete)
    }

    @Synchronized
    fun branchExist(branchName: String): Boolean {
        return exists(gitDir.resolve(BRANCHES_PATH).resolve(branchName))
    }

    @Synchronized
    fun newBranch(branchName: String) {
        if (branchExist(branchName)) {
            throw IllegalArgumentException("A branch named '$branchName' already exists.")
        }
        createBranch(branchName, commit).writeToDisk()
    }

    @Synchronized
    fun getBranches(): List<String> {
        val branchesDir = gitDir.resolve(BRANCHES_PATH)
        return newDirectoryStream(branchesDir).map { it.fileName.toString() }
    }

    @Synchronized
    fun add(path: Path) {
        for (file in walkExclude(path, onlyFiles = true)) {
            IndexEntry.EditFile(this, root.relativize(file), readAllBytes(file)).writeToDisk()
        }
        indexCache.reset()
    }

    /**
     * Add all unstaged changes to index.
     */
    @Synchronized
    fun addAll() = add(root)

    /**
     * Add pattern to ignore file in the root directory.
     */
    @Synchronized
    fun addToIgnore(vararg patterns: String) {
        Ignore.addToRoot(root, *patterns)
        ignoreCache.reset()
    }

    /**
     * Returns true if there is a githw repository in the current dir.
     */
    @Synchronized
    fun isInitialized(): Boolean = exists(gitDir)

    /**
     * Merge current branch with another.
     *
     * @param[otherBranchName] name of a branch to merge with
     * @param[message] message for the merge-commit
     * @param[author] set the author of the merge-commit
     * @param[failOnConflict] if this parameter is set, merge will be stopped in case of any conflicts
     *
     * @return list of new files, which have been created due to conflicts
     */
    @Synchronized
    fun merge( otherBranchName: String,
               message: String? = null,
               author: String? = defaultAuthor(),
               failOnConflict: Boolean = false ): List<Path> {

        checkUpToDate()
        val otherCommit = loadBranch(otherBranchName).commit
        val otherTree = otherCommit.rootTree
        val newFiles = tree.mergeWith(otherTree, root)

        if (newFiles.isNotEmpty() && failOnConflict) {
            return newFiles
        }

        val msg = message ?: run {
            val head = head
            if (head is Head.BranchPointer) {
                "Merge branches ${head.branch.name} and $otherBranchName"
            } else {
                "Merge commit ${commit.sha256} and branch $otherBranchName"
            }
        }

        val parents = immutableListOf(commit, otherCommit)
        add(root)
        commit(createCommit(msg, parents, treeWithIndex, Date(), author ?: defaultAuthor()))
        return newFiles
    }

    /**
     * Check that there are no untracked files in the working directory.
     *
     * @throws IllegalArgumentException if check has failed.
     */
    @Synchronized
    fun checkInitialized() {
        if (!isInitialized()) {
            throw IllegalArgumentException("Not a $APP_NAME repository")
        }
    }

    /**
     * Check that index is empty.
     *
     * @throws IllegalArgumentException if check has failed.
     */
    @Synchronized
    fun checkEmptyIndex() {
        if (index.isNotEmpty()) {
            throw IllegalArgumentException("Index must be empty. Commit or revert changes")
        }
    }

    /**
     * Check that there are no untracked files in the working directory.
     *
     * @throws IllegalArgumentException if check has failed.
     */
    @Synchronized
    fun checkNoUntrackedFiles() {
        if (getUntrackedAndUpdatedFiles(root).isNotEmpty()) {
            throw IllegalArgumentException("Some changes are untracked. Commit or revert changes.")
        }
    }

    /**
     * Check that repository is up to date. I.e. there are no untracked files and index is empty.
     *
     * @throws IllegalArgumentException if check has failed.
     */
    @Synchronized
    fun checkUpToDate() {
        checkEmptyIndex()
        checkNoUntrackedFiles()
    }

    /**
     * Walk a file tree, ignore files form [ignore].
     *
     * @param[start] the starting path
     * @param[childrenFirst] if this parameter is set,
     * output will contain all children of a tree before the tree itself.
     * @param[onlyFiles] if this parameter is set, output will contain only files
     */
    @Synchronized
    fun walkExclude(start: Path, childrenFirst: Boolean = false, onlyFiles: Boolean = false ): List<Path> {

        val res = ArrayList<Path>()

        fun impl(current: Path) {
            if (ignore.contains(root.relativize(current))) {
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

        impl(start)
        return res
    }
}
