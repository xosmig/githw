package com.xosmig.githw.cli

import java.nio.file.Paths

/**
 * Adds file's contents to the index.
 */
internal class AddAction : Action("Add file's content to the index", "add") {
    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            tooFewArguments(atLeast = 1, actual = 0)
        }
        for (file in args) {
            githw.add(Paths.get(file))
        }
    }
}

