package com.xosmig.githw.objects

import com.github.andrewoma.dexx.kollection.ImmutableMap
import com.github.andrewoma.dexx.kollection.immutableMapOf
import com.github.andrewoma.dexx.kollection.toImmutableMap
import com.xosmig.githw.controller.GithwController
import com.xosmig.githw.objects.GitObjectFromDisk.Companion.getObjectFromDisk
import java.io.*
import java.nio.file.Path
import java.util.*
import com.xosmig.githw.utils.Sha256
import com.xosmig.githw.objects.GitFile.Companion.createFile

class GitTree private constructor( githw: GithwController,
                                   val children: ImmutableMap<String, GitObject>,
                                   knownSha256: Sha256? ): GitFSObject(githw, knownSha256) {

    companion object {
        internal fun load(githw: GithwController, sha256: Sha256, ins: ObjectInputStream): GitTree {
            val count = ins.readInt()
            val children = HashMap<String, GitObjectFromDisk>()
            for (i in 1..count) {
                val name = ins.readObject() as String
                children[name] = githw.getObjectFromDisk(ins.readObject() as Sha256)
            }
            return GitTree(githw, children.toImmutableMap(), sha256)
        }

        fun GithwController.createEmptyTree(): GitTree {
            return GitTree(this, immutableMapOf(), knownSha256 = null)
        }

        fun GithwController.createTree(children: ImmutableMap<String, GitObject>): GitTree {
            return GitTree(this, children, knownSha256 = null)
        }

        fun GithwController.createTree(children: Map<String, GitObject>): GitTree {
            return createTree(children.toImmutableMap())
        }
    }

    // unfortunately, there is no visibility modifier to allow to access the constructor only the parent class
    class Result<out T> internal constructor(val modifiedTree: GitTree, val value: T) {
        operator fun component1() = modifiedTree
        operator fun component2() = value
    }

    /**
     * Resolve the given path to a subdirectory.
     *
     * @param[path] relative path to the subdirectory. Only directories' names are allowed.
     * `null` interpreted as an empty path.
     * @return null if the subdirectory is missing. Corresponding `GitTree` otherwise.
     */
    fun resolve(path: Path?): GitObject? {
        val normalizedPath = path?.normalize()
        if (normalizedPath == null || normalizedPath.fileName.toString() == "") {
            return this
        }
        val name = normalizedPath.fileName.toString()
        var res: GitObject? = null
        resolveDir(normalizedPath.parent, false) {
            res = it.getChild(name)
            it
        }
        return res
    }

    /**
     * Create subdirectories (if missing) according to the given path.
     *
     * @param[path] relative path to create. Only directories' names are allowed. `null` interpreted as an empty path.
     * @return `GitTree` object connected with the given path.
     */
    fun createPath(path: Path?, operation: (GitTree) -> GitTree = {it}): Result<GitTree> {
        return resolveDir(path, true, operation)
                ?: throw IllegalArgumentException("Invalid pathToFile: '$path'")
    }

    /**
     * @param[path] path to a subdirectory to resolve. Only directories' names are allowed.
     * `null` interpreted as an empty path.
     * @return null if the subdirectory is missing. Corresponding `GitTree` otherwise.
     */
    private fun resolveDir( path: Path?,
                            createMissing: Boolean,
                            operation: (GitTree) -> GitTree ): Result<GitTree>? {
        val normalizedPath = path?.normalize()
        val pathList = if (normalizedPath == null || normalizedPath.fileName.toString() == "") {
            emptyList()
        } else {
            normalizedPath.toList()
        }
        return resolveDirImpl(pathList, createMissing, operation)
    }

    private fun resolveDirImpl( path: List<Path>,
                                createMissing: Boolean,
                                operation: (GitTree) -> GitTree ): Result<GitTree>? {
        if (path.isEmpty()) {
            val modified = operation(this)
            return Result(modifiedTree = modified, value = modified)
        }
        val nextName = path.first().toString()
        val child = children[nextName]?.loaded
        val next: GitTree = when {
            child == null && createMissing -> githw.createEmptyTree()
            child is GitTree -> child
            else -> return null
        }
        val (newNext, res) =  next.resolveDirImpl(path.subList(1, path.size), createMissing, operation)
                ?: return null
        return Result(modifiedTree = putChild(nextName, newNext), value = res)
    }

    fun putFile(path: Path, content: ByteArray): GitTree {
        return createPath(path.parent) {
            it.putChild(path.fileName.toString(), githw.createFile(content))
        }.modifiedTree
    }

    private fun removeChild(name: String): GitTree = githw.createTree(children.minus(name))

    private fun putChild(name: String, child: GitObject): GitTree = githw.createTree(children.put(name, child))

    fun containsFile(file: Path): Boolean = resolve(file)?.loaded is GitFile

    fun removeFile(path: Path): GitTree {
        val name = path.fileName.toString()
        return createPath(path.parent) {
            if (!it.children.containsKey(name)) {
                throw IllegalArgumentException("Invalid path to deleteRecursive: '$path'")
            }
            it.removeChild(name)
        }.modifiedTree
    }

    fun getChild(name: String): GitObject? = children[name]

    @Throws(IOException::class)
    override fun writeContentTo(out: ObjectOutputStream) {
        out.writeInt(children.size)
        for ((name, obj) in children) {
            out.writeObject(name)
            out.writeObject(obj.sha256)
        }
    }

    override fun writeToDiskImpl() {
        super.writeToDiskImpl()
        for (child in children.values) {
            child.writeToDisk()
        }
    }

    override fun restore(path: Path) {
        for ((name, child) in children) {
            val loaded = child.loaded
            if (loaded !is GitFSObject) {
                throw IllegalStateException("Unknown child type in `GitTree`: '${loaded.javaClass.name}'")
            }
            loaded.restore(path.resolve(name))
        }
    }

    override fun toString(): String = children.mapValues { it.value.loaded.toString() }.toList().toString()

    override fun mergeWith(other: GitFSObject, path: Path, newFiles: MutableList<Path>) {
        if (other.sha256 == sha256) {
            return
        }
        if (other is GitTree) {
            for ((name, otherChildNotLoaded) in other.children) {
                val otherChild = otherChildNotLoaded.loaded as GitFSObject
                val child = getChild(name)?.loaded as? GitFSObject
                if (child != null) {
                    child.mergeWith(otherChild, path.resolve(name))
                } else {
                    otherChild.restore(path.resolve(name))
                }
            }
        } else if (other is GitFile) {
            other.restore(resolveConflictPath(path, newFiles))
        } else {
            throw IllegalStateException("Unknown file system type: '${loaded.javaClass.name}'")
        }
    }
}
