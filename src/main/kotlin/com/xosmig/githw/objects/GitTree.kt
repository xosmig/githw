package com.xosmig.githw.objects

import org.apache.commons.codec.digest.DigestUtils
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class GitTree private constructor(gitDir: Path, children: Map<String, GitObject>): GitObject(gitDir) {

    private val children: MutableMap<String, GitObject> = HashMap(children)

    override val loaded: GitObject = this

    /**
     * TODO
     */
    fun createPath(path: Path?): GitTree = createPathImpl(path?.normalize())

    /**
     * @param[path] normalized path to resolve
     */
    private fun createPathImpl(path: Path?): GitTree {
        if (path == null || path == emptyNormalizedPath) {
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

    fun putFile(path: Path, file: GitFile) {
        val dir = createPath(path.parent)
        dir.children.put(path.fileName.toString(), file)
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

    override fun sha256(): String {
        ByteArrayOutputStream().use {
            val baos = it
            ObjectOutputStream(baos).use {
                writeContentTo(it)
            }
            return DigestUtils.sha256Hex(baos.toByteArray())
        }
    }

    @Throws(IOException::class)
    private fun writeContentTo(out: ObjectOutputStream) {
        out.writeInt(children.size)
        for ((name, obj) in children) {
            out.writeObject(name)
            out.writeObject(obj.sha256())
        }
    }

    @Throws(IOException::class)
    override fun writeToDisk() {
        for (child in children.values) {
            child.writeToDisk()
        }
        Files.newOutputStream(getObjectFile()).use {
            ObjectOutputStream(it).use {
                it.writeObject(javaClass.name)
                writeContentTo(it)
            }
        }
    }

    companion object {
        private val emptyNormalizedPath = Paths.get(".").normalize()

        fun load(gitDir: Path, ins: ObjectInputStream): GitTree {
            val count = ins.readInt()
            val children = HashMap<String, GitObjectNotLoaded>()
            for (i in 1..count) {
                val name = ins.readObject() as String
                children[name] = GitObjectNotLoaded(gitDir, ins.readObject() as String)
            }
            return GitTree(gitDir, children)
        }
    }
}
