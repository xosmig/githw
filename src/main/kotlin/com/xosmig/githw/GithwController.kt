package com.xosmig.githw

import com.github.andrewoma.dexx.kollection.immutableListOf
import com.xosmig.githw.index.Index
import com.xosmig.githw.index.IndexEntry
import com.xosmig.githw.objects.Commit
import com.xosmig.githw.objects.Commit.Companion.defaultAuthor
import com.xosmig.githw.objects.GitFSObject
import com.xosmig.githw.objects.GitFile
import com.xosmig.githw.objects.GitTree
import com.xosmig.githw.refs.Branch
import com.xosmig.githw.refs.Head
import com.xosmig.githw.utils.Cache
import com.xosmig.githw.utils.FilesUtils
import com.xosmig.githw.utils.FilesUtils.isEmptyDir
import java.nio.file.Files.*
import java.nio.file.Path
import java.util.*

/**
 * Provides most common commands to work with a repository.
 */
class GithwController(var root: Path) {

    // independent caches: head, index, ignore

    val gitDir = root.resolve(GIT_DIR_PATH)!!
    val initialized = Cache({ checkInitialized() })

    val headCache = Cache({ Head.load(gitDir) }, initialized)
    val head by headCache

    val commitCache = Cache({ head.commit }, headCache)
    val commit by commitCache

    val treeCache = Cache({ commit.rootTree }, commitCache)
    val tree get() = commit.rootTree

    val indexCache = Cache({ Index.load(gitDir) }, initialized)
    val index by indexCache

    val ignoreCache = Cache({ Ignore.loadFromRoot(root) }, initialized)
    val ignore by ignoreCache

    val treeWithIndexCache = Cache({ index.applyToTree(tree) }, indexCache, treeCache)
    val treeWithIndex by treeWithIndexCache

    /**
     * Create an empty repository in the given directory.
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

        val commit = Commit.create(gitDir,
                message = "Initial commit",
                parents = emptyList(),
                rootTree = GitTree.create(gitDir, emptyMap()),
                date = Date()
        )
        val branch = Branch(gitDir, "master", commit)
        branch.writeToDisk()
        Head.BranchPointer(gitDir, branch).writeToDisk()
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
        commit(Commit.create(gitDir, message, listOf(commit), treeWithIndex, date, author))
    }

    @Synchronized
    fun remove(path: Path) {
        if (!exists(path)) {
            throw IllegalArgumentException("Invalid path '$path'")
        }
        for (current in walkExclude(path, childrenFirst = true, onlyFiles = false)) {
            if (isRegularFile(current)) {
                IndexEntry.RemoveFile(gitDir, root.relativize(current)).writeToDisk()
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

    @Synchronized
    fun switchBranch(branchName: String) {
        checkUpToDate()
        if (Index.load(gitDir).isNotEmpty()) {
            throw IllegalArgumentException("Index is not empty")
        }
        Head.BranchPointer(gitDir, Branch.load(gitDir, branchName)).writeToDisk()
        revert(root)

        headCache.reset()
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
        Branch(gitDir, branchName, commit).writeToDisk()
    }

    @Synchronized
    fun getBranches(): List<String> {
        val branchesDir = gitDir.resolve(BRANCHES_PATH)
        return newDirectoryStream(branchesDir).map { it.fileName.toString() }
    }

    @Synchronized
    fun add(path: Path) {
        for (file in walkExclude(path, onlyFiles = true)) {
            IndexEntry.EditFile(gitDir, root.relativize(file), readAllBytes(file)).writeToDisk()
        }
        indexCache.reset()
    }

    @Synchronized
    fun addAll() = add(root)

    @Synchronized
    fun addToIgnore(vararg patterns: String) {
        Ignore.addToRoot(root, *patterns)
        ignoreCache.reset()
    }

    @Synchronized
    fun isInitialized(): Boolean = exists(gitDir)

    /**
     * Merge current branch with another.
     *
     * @param[otherBranchName] Name of a branch to merge with
     * @return List of new files, which have been created due to conflicts
     */
    @Synchronized
    fun merge( otherBranchName: String,
               message: String? = null,
               author: String? = defaultAuthor(),
               failOnConflict: Boolean = false ): List<Path> {

        checkUpToDate()
        val otherCommit = Branch.load(gitDir, otherBranchName).commit
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
        commit(Commit.create(gitDir, msg, parents, treeWithIndex, Date(), author ?: defaultAuthor()))
        return newFiles
    }

    @Synchronized
    fun checkInitialized() {
        if (!isInitialized()) {
            throw IllegalArgumentException("Not a $APP_NAME repository")
        }
    }

    @Synchronized
    fun checkEmptyIndex() {
        if (index.isNotEmpty()) {
            throw IllegalArgumentException("Index must be empty. Commit or revert changes")
        }
    }

    @Synchronized
    fun checkUntrackedFiles() {
        if (getUntrackedAndUpdatedFiles(root).isNotEmpty()) {
            throw IllegalArgumentException("Some changes are untracked. Commit or revert changes.")
        }
    }

    @Synchronized
    fun checkUpToDate() {
        checkEmptyIndex()
        checkUntrackedFiles()
    }

    @Synchronized
    fun walkExclude(path: Path,
                    childrenFirst: Boolean = false,
                    onlyFiles: Boolean = false,
                    ignore: Ignore = Ignore.loadFromRoot(root) ): List<Path> {

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

        impl(path)
        return res
    }
}
