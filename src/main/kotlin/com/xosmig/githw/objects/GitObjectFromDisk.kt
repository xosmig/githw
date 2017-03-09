package com.xosmig.githw.objects

import com.xosmig.githw.utils.Sha256
import java.io.ObjectInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicBoolean

class GitObjectFromDisk(gitDir: Path, private val sha256: Sha256): GitObject(gitDir) {
    override fun getSha256(): Sha256 = if (isLoaded) { loaded.getSha256() } else { sha256 }

    override fun writeToDisk() {
        if (isLoaded) {
            loaded.writeToDisk()
        }
    }

    private val isLoadedField = AtomicBoolean(false)
    val isLoaded: Boolean
        get() = isLoadedField.get()

    override val loaded: GitObjectLoaded by lazy {
        val res = Files.newInputStream(getObjectFile()).use {
            ObjectInputStream(it).use {
                val type = it.readObject() as String
                when (type) {
                    GitFile::class.java.name -> GitFile.load(gitDir, it)
                    GitTree::class.java.name -> GitTree.load(gitDir, it)
                    Commit::class.java.name -> Commit.load(gitDir, it)
                    else -> throw ClassNotFoundException("'$type' is not a valid GitObject class")
                }
            }
        }
        isLoadedField.set(true)
        res
    }


}
