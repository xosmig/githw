package com.xosmig.githw.cli

internal class MergeAction : Action("Remove files from the working tree", "remove", "delete", "rm", "del") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, exact = 1)
        githw.merge(args[0])
    }
}

