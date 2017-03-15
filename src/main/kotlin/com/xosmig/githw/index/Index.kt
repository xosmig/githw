package com.xosmig.githw.index

import com.xosmig.githw.INDEX_PATH
import java.nio.file.Files.*
import java.nio.file.Path
import com.xosmig.githw.index.IndexEntry.*
import com.xosmig.githw.objects.GitTree

class Index private constructor(entries: List<IndexEntry>): List<IndexEntry> by entries {

    companion object {
        /**
         * Remove redundant entries and create Index object, which contains all relevant index entries.
         */
        fun load(gitDir: Path): Index {
            val indexDir = gitDir.resolve(INDEX_PATH)

            val (relevantNumbers, relevantEntries) = newDirectoryStream(indexDir)
                    .map { Pair(it.fileName.toString().toInt(), IndexEntry.load(gitDir, it)) }
                    .sortedBy { (num, _) -> num }
                    .associateBy { (_, entry) -> entry.pathToFile }
                    .values.unzip()

            // remove irrelevant entries
            IntRange(1, relevantNumbers.max() ?: -1)
                    .filterNot { relevantNumbers.contains(it) }
                    .forEach { delete(indexDir.resolve(it.toString())) }

            return Index(relevantEntries)
        }

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
