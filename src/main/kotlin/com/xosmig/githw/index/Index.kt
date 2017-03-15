package com.xosmig.githw.index

import com.xosmig.githw.INDEX_PATH
import java.io.IOException
import java.nio.file.Files.*
import java.nio.file.Path
import com.xosmig.githw.index.IndexEntry.*
import com.xosmig.githw.objects.GitTree

class Index private constructor(entries: List<IndexEntry>): List<IndexEntry> by entries {

    companion object {
        @Throws(IOException::class)
        fun load(gitDir: Path): Index {
            val files = newDirectoryStream(gitDir.resolve(INDEX_PATH))
                    .sortedBy { it.fileName.toString().toInt() }
            val entries = files.map { IndexEntry.load(gitDir, it) }
            return Index(entries)
        }

        @Throws(IOException::class)
        fun clear(gitDir: Path) {
            for (file in newDirectoryStream(gitDir.resolve(INDEX_PATH))) {
                delete(file)
            }
        }
    }

    fun applyToTree(tree: GitTree): GitTree {
        var res = tree
        for (entry in this) {
            when (entry) {
                is EditFile -> res = res.putFile(entry.pathToFile, entry.content)
                is RemoveFile -> {
                    if (res.containsFile(entry.pathToFile)) {
                        res = res.removeFile(entry.pathToFile)
                    }
                }
                else -> throw UnsupportedOperationException("Unknown IndexEntry type: '${entry.javaClass.name}'")
            }
        }
        return res
    }
}
