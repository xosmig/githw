package com.xosmig.githw.objects

import com.xosmig.githw.HASH_PREF_LENGTH
import org.apache.commons.codec.digest.DigestUtils
import java.io.ByteArrayOutputStream
import java.io.IOError
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class GitTree internal constructor(children: Map<String, GitObject>): GitObject() {
    companion object {
        private val emptyNormalizedPath = Paths.get(".").normalize()
    }

    private val children = HashMap(children)

    fun createPath(path: Path?): GitTree = createPathImpl(path?.normalize())

    /**
     * @param[path] normalized path to resolve
     */
    private fun createPathImpl(path: Path?): GitTree {
        if (path == null || path == emptyNormalizedPath) {
            return this
        }
        val nextPath = path.first()
        val next = children[nextPath.toString()]?.load()
        val result = when (next) {
            null -> GitTree(emptyMap())
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
            val next = children[nextPath.toString()]?.load() as? GitTree
                    ?: throw IllegalArgumentException("Invalid path: '$path'")
            next.removeFile(nextPath.relativize(path))
            if (next.children.isEmpty()) {
                children.remove(nextPath.toString())
            }
        }
    }

    private fun content(): ByteArray {
        // unfortunately, Kotlin still doesn't have any good syntax to handle multiple resources
        ByteArrayOutputStream().use {
            val baos = it
            ObjectOutputStream(baos).use {
                it.writeInt(children.size)
                for ((name, obj) in children) {
                    it.writeObject(name)
                    it.writeObject(obj.sha256())
                }
            }
            return baos.toByteArray()
        }
    }

    override fun sha256(): String = DigestUtils.sha256Hex(content())

    @Throws(IOError::class)
    private fun writeToFile(path: Path) {
        Files.write(path, content())
    }

    @Throws(IOError::class)
    override fun writeToDisk(objectsDir: Path) {
        for (child in children.values) {
            child.writeToDisk(objectsDir)
        }
        val sha256 = sha256()
        val dir = objectsDir.resolve(sha256.take(HASH_PREF_LENGTH))
        Files.createDirectory(dir)
        writeToFile(dir.resolve(sha256.drop(HASH_PREF_LENGTH)))
    }
}
