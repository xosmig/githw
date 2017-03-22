package com.xosmig.githw.objects

import com.xosmig.githw.controller.GithwController
import com.xosmig.githw.utils.Sha256
import java.io.ObjectInputStream
import java.nio.file.Files.*
import com.xosmig.githw.objects.GitFile.Companion.loadFile
import com.xosmig.githw.objects.GitTree.Companion.loadTree
import com.xosmig.githw.objects.Commit.Companion.loadCommit

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
                        GitFile::class.java.name -> githw.loadFile(sha256, it)
                        GitTree::class.java.name -> githw.loadTree(sha256, it)
                        Commit::class.java.name -> githw.loadCommit(sha256, it)
                        else -> throw ClassNotFoundException("'$type' is not a valid GitObject class")
                    }
                }
            }
        }
}
