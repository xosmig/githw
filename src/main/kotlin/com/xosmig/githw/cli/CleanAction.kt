package com.xosmig.githw.cli

import java.nio.file.Paths

internal class CleanAction : Action("Remove untracked files from the working tree", "clean") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, exact = 1)

        if (args[0] == "--all") {
            githw.clean(githw.root)
        } else {
            githw.clean(Paths.get(args[0]))
        }
    }
}
