package com.xosmig.githw.objects

import com.xosmig.githw.controller.GithwController
import com.xosmig.githw.utils.Sha256
import java.io.ObjectInputStream
import java.nio.file.Files.*

class GitObjectFromDisk private constructor(githw: GithwController, override val sha256: Sha256): GitObject(githw) {

    companion object {
        fun GithwController.getObjectFromDisk(sha256: Sha256): GitObjectFromDisk = GitObjectFromDisk(this, sha256)
        fun GithwController.loadObject(sha256: Sha256): GitObjectLoaded = getObjectFromDisk(sha256).loaded
    }

    override fun writeToDisk() = Unit

    override val loaded: GitObjectLoaded
        get() = githw.loadedCache.getOrPut(sha256) {
            newInputStream(objectFile).use {
                ObjectInputStream(it).use {
                    val type = it.readObject() as String
                    when (type) {
                        GitFile::class.java.name -> GitFile.load(githw, sha256, it)
                        GitTree::class.java.name -> GitTree.load(githw, sha256, it)
                        Commit::class.java.name -> Commit.load(githw, sha256, it)
                        else -> throw ClassNotFoundException("'$type' is not a valid GitObject class")
                    }
                }
            }
        }
}
