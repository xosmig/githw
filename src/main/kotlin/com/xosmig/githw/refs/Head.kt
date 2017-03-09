/*
package com.xosmig.githw.refs

import com.xosmig.githw.HEAD_PATH
import java.io.IOException
import java.nio.file.Path


/**
 * HEAD is either a pointer to a ref or a pointer to a commit (detached HEAD).
 */

abstract class Head private constructor(private val gitDir: Path) {
    companion object {
        @Throws(IOException::class)
        fun load(gitDir: Path): Head {
//            val path = gitDir.resolve(HEAD_PATH)
//            // TODO
//            return Head(gitDir)
        }
    }

//    class Ref(gitDir: Path): Head(gitDir)
//    class CommitPointer(gitDir: Path, commitHash: Sha256): Head(gitDir)

//
//    fun getCommit(): Commit {
//        // TODO
//        throw UnsupportedOperationException("not implemented")
//    }
//
//    fun writeToDisk() {
//
//    }
}
*/
