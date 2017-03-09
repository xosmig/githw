package com.xosmig.githw.refs

import com.xosmig.githw.BRANCHES_PATH
import com.xosmig.githw.objects.Commit
import com.xosmig.githw.objects.GitObjectFromDisk
import com.xosmig.githw.utils.Sha256
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Path

class Branch(private val gitDir: Path, val name: String, commit: Commit) {
    var commit = commit
        private set

    companion object {
        @Throws(IOException::class)
        fun load(gitDir: Path, branchName: String): Branch {
            Files.newInputStream(gitDir.resolve(BRANCHES_PATH).resolve(branchName)).use {
                ObjectInputStream(it).use {
                    val commit = GitObjectFromDisk(gitDir, it.readObject() as Sha256).loaded as Commit
                    return Branch(gitDir, branchName, commit)
                }
            }
        }
    }

    @Throws(IOException::class)
    fun writeToDisk() {
        commit.writeToDisk()
        Files.newOutputStream(gitDir.resolve(BRANCHES_PATH).resolve(name)).use {
            ObjectOutputStream(it).use {
                it.writeObject(commit.getSha256())
            }
        }
    }
}
