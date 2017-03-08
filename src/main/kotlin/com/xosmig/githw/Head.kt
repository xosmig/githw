package com.xosmig.githw

import com.xosmig.githw.refs.Commit
import java.io.IOException
import java.nio.file.Path

/**
 * HEAD is either a pointer to a ref or a pointer to a commit (detached HEAD).
 */
class Head {
    companion object {
        @Throws(IOException::class)
        fun readFromFile(path: Path): Head {
            // TODO
            throw UnsupportedOperationException("not implemented")
        }
    }

    fun getCommit(): Commit {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }
}
