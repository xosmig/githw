package com.xosmig.githw.index

import com.xosmig.githw.INDEX_PATH
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import com.xosmig.githw.index.IndexEntry.*
import com.xosmig.githw.objects.GitTree

class Index private constructor() {
    private val entries = ArrayList<IndexEntry>()

    companion object {
        @Throws(IOException::class)
        fun load(gitDir: Path): Index {
            val res = Index()
            val files = Files.newDirectoryStream(gitDir.resolve(INDEX_PATH)).sortedBy {
                it.fileName.toString().toInt()
            }
            for (file in files) {
                res.entries.add(IndexEntry.load(gitDir, file))
            }
            return res
        }

        @Throws(IOException::class)
        fun clear(gitDir: Path) {
            for (file in Files.newDirectoryStream(gitDir.resolve(INDEX_PATH))) {
                Files.delete(file)
            }
        }
    }

    fun applyToTree(tree: GitTree) {
        for (entry in entries) {
            when (entry) {
                is IndexEditFile -> tree.putFile(entry.pathToFile, entry.content)
                is IndexRemoveFile -> tree.removeFile(entry.pathToFile)
                else -> throw UnsupportedOperationException("Unknown IndexEntry type: '${entry.javaClass.name}'")
            }
        }
    }
}
