package com.xosmig.githw.objects

import java.io.*
import java.nio.file.Path
import java.util.*
import com.xosmig.githw.utils.Sha256

class GitTree(gitDir: Path, children: Map<String, GitObject>): GitObjectLoaded(gitDir) {

    val children: MutableMap<String, GitObject> = HashMap(children)

    companion object {
        fun load(gitDir: Path, ins: ObjectInputStream): GitTree {
            val count = ins.readInt()
            val children = HashMap<String, GitObjectFromDisk>()
            for (i in 1..count) {
                val name = ins.readObject() as String
                children[name] = GitObjectFromDisk(gitDir, ins.readObject() as Sha256)
            }
            return GitTree(gitDir, children)
        }
    }

    /**
     * Resolve the given path to a subdirectory.
     *
     * @param[path] path to the subdirectory. Only directories' names are allowed. `null` interpreted as an empty path.
     * @return null if the subdirectory is missing. Corresponding `GitTree` otherwise.
     */
    fun resolve(path: Path?): GitObject? {
        if (path == null) {
            return this
        }
        val dir = resolveDir(path.parent?.normalize(), createMissing = false)
        return dir?.getChild(path.fileName.toString())
    }

    /**
     * Create subdirectories (if missing) according to the given path.
     *
     * @param[path] path to create. Only directories' names are allowed. `null` interpreted as an empty path.
     * @return `GitTree` object connected with the given path.
     */
    fun createPath(path: Path?): GitTree = resolveDir(path?.normalize(), createMissing = true)
        ?: throw IllegalArgumentException("Invalid pathToFile: '$path'")

    /**
     * @param[path] normalized path to a subdirectory to resolve. Only directories' names are allowed.
     * `null` interpreted as an empty path.
     * @return null if the subdirectory is missing. Corresponding `GitTree` otherwise.
     */
    private fun resolveDir(path: Path?, createMissing: Boolean): GitTree? {
        if (path == null || path.fileName.toString() == "") {
            return this
        }
        return resolveDirImpl(path.toList(), createMissing)
    }

    private fun resolveDirImpl(path: List<Path>, createMissing: Boolean): GitTree? {
        if (path.isEmpty()) {
            return this
        }
        val nextPath = path.first()
        val next = children[nextPath.toString()]?.loaded
        val result = when {
            next == null && createMissing -> GitTree(gitDir, HashMap())
            next is GitTree -> next
            else -> return null
        }
        children[nextPath.toString()] = result
        return result.resolveDirImpl(path.subList(1, path.size), createMissing)
    }

    fun putFile(path: Path, content: ByteArray) {
        val dir = createPath(path.parent)
        dir.children.put(path.fileName.toString(), GitFile(gitDir, content))
    }

    fun removeFile(path: Path) {
        if (path.parent == null) {
            if (children.remove(path.toString()) == null) {
                throw IllegalArgumentException("No such file: '$path'")
            }
        } else {
            val nextPath = path.first()
            val next = children[nextPath.toString()]?.loaded as? GitTree
                    ?: throw IllegalArgumentException("Invalid pathToFile: '$path'")
            next.removeFile(nextPath.relativize(path))
            if (next.children.isEmpty()) {
                children.remove(nextPath.toString())
            }
        }
    }

    fun getChild(name: String): GitObject = children[name]
            ?: throw NoSuchElementException("Child '$name' not found")

    @Throws(IOException::class)
    override fun writeContentTo(out: ObjectOutputStream) {
        out.writeInt(children.size)
        for ((name, obj) in children) {
            out.writeObject(name)
            out.writeObject(obj.getSha256())
        }
    }

    override fun writeToDisk() {
        super.writeToDisk()
        for (child in children.values) {
            child.writeToDisk()
        }
    }
}
