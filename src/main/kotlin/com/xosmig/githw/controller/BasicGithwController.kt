package com.xosmig.githw.controller

import com.github.andrewoma.dexx.kollection.immutableListOf
import com.xosmig.githw.*
import com.xosmig.githw.index.Index
import com.xosmig.githw.index.IndexEntry
import com.xosmig.githw.objects.Commit
import com.xosmig.githw.objects.Commit.Companion.createCommitObject
import com.xosmig.githw.objects.Commit.Companion.defaultAuthor
import com.xosmig.githw.objects.GitFSObject
import com.xosmig.githw.objects.GitFile
import com.xosmig.githw.objects.GitObjectFromDisk.Companion.loadObject
import com.xosmig.githw.objects.GitTree.Companion.createEmptyTree
import com.xosmig.githw.refs.Branch
import com.xosmig.githw.refs.Branch.Companion.branchExists
import com.xosmig.githw.refs.Branch.Companion.createBranchObject
import com.xosmig.githw.refs.Branch.Companion.loadBranch
import com.xosmig.githw.refs.Head
import com.xosmig.githw.utils.FilesUtils
import com.xosmig.githw.utils.FilesUtils.isEmptyDir
import com.xosmig.githw.utils.Sha256
import com.xosmig.githw.utils.cache
import java.nio.file.Files.*
import java.nio.file.Path
import java.util.*

/**
 * Provides most common controller to work with a repository.
 */
class BasicGithwController(root: Path): GithwController {

    companion object {
        /**
         * Create an empty repository in [root].
         *
         * @param[root] path for a new repository
         * @return [BasicGithwController] over the new repository
         */
        fun init(root: Path): BasicGithwController {
            val gitDir = root.resolve(GIT_DIR_PATH)
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

            val res = BasicGithwController(root)

            val commit = res.createCommitObject(
                    message = "Initial commit",
                    parents = emptyList(),
                    rootTree = res.createEmptyTree(),
                    date = Date()
            )
            val branch = res.createBranchObject("master", commit)
            branch.writeToDisk()
            res.writeToHead(branch)

            return res
        }

        fun isInitializedIn(root: Path): Boolean = exists(root.resolve(GIT_DIR_PATH))
    }

    override val root: Path = root.toAbsolutePath()
    override val gitDir: Path = root.resolve(GIT_DIR_PATH)

    override val loadedBank: LoadedObjectsBank = LoadedObjectsCachePermanent()

    // independent caches: head, index, ignore

    private val headCache = cache { Head.load(this) }
    override val head by headCache

    private val commitCache = cache(headCache) { head.commit }
    override val commit by commitCache

    private val treeCache = cache(commitCache) { commit.rootTree }
    override val tree get() = commit.rootTree

    private val indexCache = cache { Index.load(this) }
    override val index by indexCache

    private val ignoreCache = cache { Ignore.loadFromRoot(root) }
    override val ignore by ignoreCache

    private val treeWithIndexCache = cache(indexCache, treeCache) { index.applyToTree(tree) }
    override val treeWithIndex by treeWithIndexCache

    init { checkInitialized() }

    @Synchronized
    fun getLog(): List<Commit> {
        val visited = HashSet<Commit>()
        val res = ArrayList<Commit>()

        fun Commit.dfs() {
            if (!visited.add(this)) { return }
            for (parent in parents) {
                (parent.loaded as Commit).dfs()
            }
            res.add(this)
        }
        commit.dfs()

        return res
    }

    @Synchronized
    fun writeToHead(branch: Branch) { Head.BranchPointer(this, branch).writeToDisk() }

    @Synchronized
    fun writeToHead(commit: Commit) { Head.CommitPointer(this, commit).writeToDisk() }

    @Synchronized
    fun detach(sha256: Sha256) {
        val toCommit = loadObject(sha256) as? Commit
                ?: throw IllegalArgumentException("Not a real commit hash: '$commit'")
        detach(toCommit)
    }

    @Synchronized
    fun detach(toCommit: Commit) {
        checkUpToDate()
        writeToHead(toCommit)
        headCache.reset()
        cleanAndResetAll()
    }

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
        commit(createCommitObject(message, listOf(commit), treeWithIndex, date, author))
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
     * Reset working tree files.
     *
     * @param[path] path to a directory or a file to reset.
     */
    @Synchronized
    fun reset(path: Path) {
        val obj = tree.resolve(root.relativize(path))?.loaded
                ?: throw IllegalArgumentException("file '$path' is not tracked")
        if (obj !is GitFSObject) {
            throw IllegalArgumentException("Bad object type to refresh: '${obj.javaClass.name}'")
        }
        obj.reset(path)
    }

    /**
     * Reset all files.
     */
    @Synchronized
    fun resetAll() { reset(root) }

    @Synchronized
    fun cleanAndResetAll() {
        resetAll()
        cleanAll()
    }

    /**
     * Switch the current branch to [branchName] and updated the working directory.
     *
     * @param[branchName] a branch to switch to.
     * @param[createIfAbsent] if this parameter is set and the required branch doesn't exist,
     * a new branch will be created
     */
    @Synchronized
    fun switchBranch(branchName: String, createIfAbsent: Boolean = false) {
        checkUpToDate()
        if (createIfAbsent && !branchExists(branchName)) {
            newBranch(branchName)
        }
        writeToHead(loadBranch(branchName))
        headCache.reset()
        cleanAndResetAll()
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
                .map { root.relativize(it) }
                .filter {
                    val gitObj = treeWithIndex.resolve(it)?.loaded
                    gitObj !is GitFile || gitObj.sha256 != FilesUtils.countSha256(root.resolve(it))
                }
    }

    @Synchronized
    fun getUntrackedFiles(path: Path): List<Path> {
        return walkExclude(path, onlyFiles = true)
                .filter { isRegularFile(it) }
                .map { root.relativize(it) }
                .filter { !treeWithIndex.containsFile(it) }
    }

    /**
     * Remove all files which are not tracked nor ignored and empty directories.
     */
    @Synchronized
    fun clean(start: Path) {
        getUntrackedFiles(start).forEach { delete(root.resolve(it)) }
        walkExclude(start, childrenFirst = true, onlyFiles = false)
                .asSequence()  // important
                .filter(::isEmptyDir)
                .forEach(::delete)
    }

    @Synchronized
    fun cleanAll() { clean(root) }

    @Synchronized
    fun newBranch(branchName: String) {
        if (branchExists(branchName)) {
            throw IllegalArgumentException("A branch named '$branchName' already exists.")
        }
        createBranchObject(branchName, commit).writeToDisk()
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
    fun isInitialized(): Boolean = isInitializedIn(root)

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
        commit(createCommitObject(msg, parents, treeWithIndex, Date(), author ?: defaultAuthor()))
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
            throw IllegalArgumentException("Index must be empty. Commit or reset changes")
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
            throw IllegalArgumentException("Some changes are untracked. Commit or reset changes.")
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
                    impl(next.toAbsolutePath())
                }
                if (childrenFirst && !onlyFiles) {
                    res.add(current)
                }
            } else {
                res.add(current)
            }
        }

        impl(start.toAbsolutePath())
        return res
    }
}
