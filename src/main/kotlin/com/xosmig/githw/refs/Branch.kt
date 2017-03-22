package com.xosmig.githw.refs

import com.xosmig.githw.controller.GithwController
import com.xosmig.githw.objects.Commit
import com.xosmig.githw.objects.GitObjectFromDisk.Companion.loadObject
import com.xosmig.githw.utils.Sha256
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files.*

class Branch private constructor(private val githw: GithwController, val name: String, val commit: Commit) {

    companion object {
        fun GithwController.loadBranch(branchName: String): Branch {
            newInputStream(branchesDir.resolve(branchName)).use {
                ObjectInputStream(it).use {
                    val commit = loadObject(it.readObject() as Sha256) as Commit
                    return Branch(this, branchName, commit)
                }
            }
        }

        fun GithwController.createBranch(name: String, commit: Commit): Branch = Branch(this, name, commit)

        internal fun loadFromHead(githw: GithwController, ins: ObjectInputStream): Branch {
            return githw.loadBranch(ins.readObject() as String)
        }
    }

    fun copy(name: String = this.name, commit: Commit = this.commit) = Branch(githw, name, commit)

    @Throws(IOException::class)
    fun writeToDisk() {
        commit.writeToDisk()
        newOutputStream(githw.branchesDir.resolve(name)).use {
            ObjectOutputStream(it).use {
                it.writeObject(commit.sha256)
            }
        }
    }

    @Throws(IOException::class)
    fun writeToHead(ins: ObjectOutputStream) = ins.writeObject(name)

    override fun toString(): String = name
}
