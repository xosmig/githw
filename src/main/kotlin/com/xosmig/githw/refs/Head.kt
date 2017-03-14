package com.xosmig.githw.refs

import com.xosmig.githw.HEAD_PATH
import com.xosmig.githw.objects.Commit
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files.*
import java.nio.file.Path

/**
 * HEAD is either a pointer to a ref or a pointer to a commit (detached HEAD).
 */
abstract class Head private constructor(protected val gitDir: Path) {
    companion object {
        @Throws(IOException::class)
        fun load(gitDir: Path): Head {
            newInputStream(gitDir.resolve(HEAD_PATH)).use {
                ObjectInputStream(it).use {
                    val type = it.readObject() as String
                    return when (type) {
                        Branch::class.java.name -> BranchPointer(gitDir, Branch.loadFromHead(gitDir, it))
                        Commit::class.java.name -> CommitPointer(gitDir, Commit.loadFromHead(gitDir, it))
                        else -> throw IllegalStateException("Unknown head type: '$type'")
                    }
                }
            }
        }
    }

    abstract fun writeToDisk()

    abstract val commit: Commit

    class BranchPointer(gitDir: Path, val branch: Branch): Head(gitDir) {
        override fun writeToDisk() {
            newOutputStream(gitDir.resolve(HEAD_PATH)).use {
                ObjectOutputStream(it).use {
                    it.writeObject(Branch::class.java.name)
                    branch.writeToHead(it)
                }
            }
        }

        override val commit: Commit = branch.commit
    }

    class CommitPointer(gitDir: Path, override val commit: Commit): Head(gitDir) {
        override fun writeToDisk() {
            newOutputStream(gitDir.resolve(HEAD_PATH)).use {
                ObjectOutputStream(it).use {
                    it.writeObject(Branch::class.java.name)
                    commit.writeToHead(it)
                }
            }
        }

        fun copy(commit: Commit = this.commit) = CommitPointer(gitDir, commit)
    }
}
