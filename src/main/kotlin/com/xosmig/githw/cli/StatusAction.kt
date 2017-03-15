package com.xosmig.githw.cli

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.index.IndexEntry
import com.xosmig.githw.refs.Head
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

internal class StatusAction : Action("Show the working tree status", "status", "stat", "st") {
    override fun run(args: List<String>) {
        if (args.isNotEmpty()) {
            throw tooManyArguments(atMost = 0, actual = args.size)
        }
        val root = Paths.get("")
        val gitDir = root.resolve(GIT_DIR_PATH)
        val head = Head.load(gitDir)
        println(head)

        val untracked = githw.getUntrackedAndUpdatedFiles(root)
        if (untracked.isNotEmpty()) {
            println("Untracked and updated files:")
            for (file in untracked) {
                println("\t$file") // TODO: red color
            }
        } else {
            println("No untracked or updated files")
        }

        if (githw.index.isNotEmpty()) {
            println("Index:")
            val lastEntries = TreeMap<Path, IndexEntry>()
            for (entry in githw.index) {
                lastEntries[entry.pathToFile] = entry
            }
            for (entry in lastEntries.values) {
                println("\t$entry")
            }
        } else {
            println("Index is empty")
        }
    }
}

