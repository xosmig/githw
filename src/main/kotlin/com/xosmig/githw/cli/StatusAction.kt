package com.xosmig.githw.cli

import com.xosmig.githw.controller.GithwController

/**
 * Action to show a working tree status.
 */
internal class StatusAction(customController: GithwController? = null) :
        ActionInitialized(customController, "Show the working tree status", "status", "stat", "st") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, exact = 0)

        println(githw.head)

        val untracked = githw.getUnstagedAndUpdatedFiles(githw.root)
        if (untracked.isNotEmpty()) {
            println("Untracked and updated files:")
            for (file in untracked.sorted()) {
                println("\t$file")
            }
        } else {
            println("No untracked or updated files")
        }

        if (githw.index.isNotEmpty()) {
            println("Index:")
            for (entry in githw.index.sortedBy { it.pathToFile }) {
                println("\t$entry")
            }
        } else {
            println("Index is empty")
        }
    }
}

