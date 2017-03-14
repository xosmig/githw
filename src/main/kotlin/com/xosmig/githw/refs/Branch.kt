package com.xosmig.githw.refs

import com.xosmig.githw.BRANCHES_PATH
import com.xosmig.githw.objects.Commit
import com.xosmig.githw.objects.GitObject
import com.xosmig.githw.utils.Sha256
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files.*
import java.nio.file.Path

class Branch(private val gitDir: Path, val name: String, val commit: Commit) {

    companion object {
        @Throws(IOException::class)
        fun load(gitDir: Path, branchName: String): Branch {
            newInputStream(gitDir.resolve(BRANCHES_PATH).resolve(branchName)).use {
                ObjectInputStream(it).use {
                    val commit = GitObject.load(gitDir, it.readObject() as Sha256) as Commit
                    return Branch(gitDir, branchName, commit)
                }
            }
        }

        @Throws(IOException::class)
        fun loadFromHead(gitDir: Path, ins: ObjectInputStream): Branch = load(gitDir, ins.readObject() as String)
    }

    fun copy(name: String = this.name, commit: Commit = this.commit) = Branch(gitDir, name, commit)

    @Throws(IOException::class)
    fun writeToDisk() {
        commit.writeToDisk()
        newOutputStream(gitDir.resolve(BRANCHES_PATH).resolve(name)).use {
            ObjectOutputStream(it).use {
                it.writeObject(commit.sha256)
            }
        }
    }

    @Throws(IOException::class)
    fun writeToHead(ins: ObjectOutputStream) = ins.writeObject(name)
}
