package com.xosmig.githw

import java.io.IOException
import java.nio.file.Path

abstract class IndexEntry(val path: Path)

class IndexEditFile(path: Path, val content: ByteArray) : IndexEntry(path)

class IndexRemoveFile(path: Path) : IndexEntry(path)

class Index(val entries: List<IndexEntry>) {
    companion object {
//        @Throws(IOException::class)
//        private fun readFromFile(path: Path): Index {
//            // TODO
//            throw UnsupportedOperationException("not implemented")
//        }

        @Throws(IOException::class)
        fun load(gitDir: Path): Index {
            // TODO
            throw UnsupportedOperationException("not implemented")
        }
    }

    @Throws(IOException::class)
    fun writeToFile(path: Path) {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }
}
