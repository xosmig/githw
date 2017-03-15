package com.xosmig.githw.cli

import java.nio.file.Paths

/**
 * Adds file's contents to the index.
 */
internal class AddAction : Action("Add file's content to the index", "add") {
    override fun run(args: List<String>) {
        checkInitialized()

        if (args.isEmpty()) {
            tooFewArguments(atLeast = 1, actual = 0)
        }

        if (args[0] == "--all" || args[0] == "-a") {
            if (args.size > 1) {
                tooManyArguments(atMost = 1, actual = args.size)
            }
            githw.addAll()
        }

        for (path in args) {
            githw.add(Paths.get(path))
        }
    }
}

