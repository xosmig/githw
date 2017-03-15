package com.xosmig.githw.cli

import java.nio.file.Paths

internal class CleanAction : Action("Remove untracked files from the working tree", "clean") {
    override fun run(args: List<String>) {
        when (args.size) {
            0 -> githw.clean(githw.root)
            1 -> githw.clean(Paths.get(args[1]))
            else -> tooManyArguments(atMost = 1, actual = args.size)
        }
    }
}
