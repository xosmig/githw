package com.xosmig.githw.objects

import com.xosmig.githw.HASH_PREF_LENGTH
import java.io.IOError
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

abstract class GitTree: GitObject() {
    abstract val loaded: GitTreeLoaded
}

private class GitTreeNotLoaded
    private constructor(private val sha256: String, private val objectsDir: Path): GitTree() {

    override val loaded: GitTreeLoaded by lazy {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }

    override fun sha256(): String = sha256

    override fun writeToDisk(objectsDir: Path) = Unit
}

class GitTreeLoaded private constructor(children: Map<String, GitObject>): GitTree() {
    companion object {
        private val emptyNormalizedPath = Paths.get(".").normalize()
    }

    override val loaded: GitTreeLoaded = this

    private val children = HashMap(children)

    fun createPath(path: Path?): GitTreeLoaded = createPathImpl(path?.normalize())

    /**
     * @param[path] normalized path to resolve
     */
    private fun createPathImpl(path: Path?): GitTreeLoaded {
        if (path == null || path == emptyNormalizedPath) {
            return this
        }
        val nextPath = path.first()
        val next = children[nextPath.toString()]
        val result = when (next) {
            null -> GitTreeLoaded(emptyMap())
            is GitTree -> next.loaded
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
            val next = children[nextPath.toString()] as? GitTree
                    ?: throw IllegalArgumentException("Invalid path: '$path'")
            next.loaded.removeFile(nextPath.relativize(path))
            if (next.loaded.children.isEmpty()) {
                children.remove(nextPath.toString())
            }
        }
    }

    private fun content(): ByteArray {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }

    override fun sha256(): String {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }

    @Throws(IOError::class)
    private fun writeToFile(path: Path) {
        // TODO
        throw UnsupportedOperationException("not implemented")
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
