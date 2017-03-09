package com.xosmig.githw.objects

import java.io.*
import java.nio.file.Path
import java.util.*
import com.xosmig.githw.utils.Sha256

class GitTree(gitDir: Path, children: Map<String, GitObject>): GitObjectLoaded(gitDir) {

    private val children: MutableMap<String, GitObject> = HashMap(children)

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
     * TODO
     */
    fun createPath(path: Path?): GitTree = createPathImpl(path?.normalize())

    /**
     * @param[path] normalized path to resolve
     */
    private fun createPathImpl(path: Path?): GitTree {
        if (path == null ||  path.fileName.toString() == "") {
            return this
        }
        val nextPath = path.first()
        val next = children[nextPath.toString()]?.loaded
        val result = when (next) {
            null -> GitTree(gitDir, HashMap())
            is GitTree -> next
            else -> throw IllegalArgumentException("Invalid path: '$path'")
        }
        children[nextPath.toString()] = result
        return result.createPathImpl(nextPath.relativize(path))
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
                    ?: throw IllegalArgumentException("Invalid path: '$path'")
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
