package com.xosmig.githw.cli

internal class StatusAction : Action("Show the working tree status", "status", "stat", "st") {
    override fun run(args: List<String>) {
        checkInitialized()
        if (args.isNotEmpty()) {
            throw tooManyArguments(atMost = 0, actual = args.size)
        }

        println(githw.head)

        val untracked = githw.getUntrackedAndUpdatedFiles(githw.root)
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

