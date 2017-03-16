package com.xosmig.githw.objects

import com.xosmig.githw.utils.Sha256
import java.io.ObjectInputStream
import java.nio.file.Files.*
import java.nio.file.Path

class GitObjectFromDisk private constructor(gitDir: Path, override val sha256: Sha256): GitObject(gitDir) {

    companion object {
        fun create(gitDir: Path, sha256: Sha256): GitObjectFromDisk = GitObjectFromDisk(gitDir, sha256)
    }

    override fun writeToDisk() = Unit

    override val loaded: GitObjectLoaded by lazy {
        val res = newInputStream(objectFile).use {
            ObjectInputStream(it).use {
                val type = it.readObject() as String
                when (type) {
                    GitFile::class.java.name -> GitFile.load(gitDir, sha256, it)
                    GitTree::class.java.name -> GitTree.load(gitDir, sha256, it)
                    Commit::class.java.name -> Commit.load(gitDir, sha256, it)
                    else -> throw ClassNotFoundException("'$type' is not a valid GitObject class")
                }
            }
        }
        res
    }
}
