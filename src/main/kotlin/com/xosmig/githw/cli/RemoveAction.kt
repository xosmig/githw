package com.xosmig.githw.cli

import java.nio.file.Paths

internal class RemoveAction : Action("Remove files from the working tree", "remove", "delete", "rm", "del") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, atLeast = 1)
        for (path in args) {
            githw.remove(Paths.get(path))
        }
    }
}
