package com.xosmig.githw.refs

import com.xosmig.githw.controller.GithwController
import com.xosmig.githw.objects.Commit
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files.*

/**
 * HEAD is either a pointer to a branch or a pointer to a commit (detached HEAD).
 */
abstract class Head private constructor(protected val githw: GithwController) {

    companion object {
        @Throws(IOException::class)
        fun load(githw: GithwController): Head {
            newInputStream(githw.headPath).use {
                ObjectInputStream(it).use {
                    val type = it.readObject() as String
                    return when (type) {
                        Branch::class.java.name -> BranchPointer(githw, Branch.loadFromHead(githw, it))
                        Commit::class.java.name -> CommitPointer(githw, Commit.loadFromHead(githw, it))
                        else -> throw IllegalStateException("Unknown head type: '$type'")
                    }
                }
            }
        }
    }

    abstract fun writeToDisk()

    abstract val commit: Commit

    class BranchPointer(githw: GithwController, val branch: Branch): Head(githw) {
        override fun writeToDisk() {
            newOutputStream(githw.headPath).use {
                ObjectOutputStream(it).use {
                    it.writeObject(Branch::class.java.name)
                    branch.writeToHead(it)
                }
            }
        }

        override val commit: Commit = branch.commit

        override fun toString(): String = "On branch $branch"
    }

    class CommitPointer(githw: GithwController, override val commit: Commit): Head(githw) {
        override fun writeToDisk() {
            newOutputStream(githw.headPath).use {
                ObjectOutputStream(it).use {
                    it.writeObject(Commit::class.java.name)
                    commit.writeToHead(it)
                }
            }
        }

        fun copy(commit: Commit = this.commit) = CommitPointer(githw, commit)

        override fun toString(): String = "Detached head: ${commit.sha256}"
    }
}
