package com.xosmig.githw.cli

import java.nio.file.Paths

/**
 * Adds file's contents to the index.
 */
internal class AddAction : Action("Add file's content to the index", "add") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, atLeast = 1)

        if (args[0] == "--all" || args[0] == "-a") {
            checkArgNumber(args.size, atMost = 1)
            githw.addAll()
        }

        for (path in args) {
            githw.add(Paths.get(path))
        }
    }
}

