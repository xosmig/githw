package com.xosmig.githw.index

import java.io.IOException
import java.nio.file.Path

abstract class IndexEntry private constructor(private val gitDir: Path, val path: Path) {
    companion object {
        @Throws(IOException::class)
        fun load(gitDir: Path, path: Path): IndexEntry {
            // TODO
            throw UnsupportedOperationException("not implemented")
        }
    }

    class IndexEditFile private constructor(gitDir: Path, pathToFile: Path, val content: ByteArray):
            IndexEntry(gitDir, pathToFile)
    class IndexRemoveFile private constructor(gitDir: Path, pathToFile: Path):
            IndexEntry(gitDir, pathToFile)
}
