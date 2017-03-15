package com.xosmig.githw.cli

import java.nio.file.Paths

internal class RevertAction : Action("Restore working tree files", "refresh", "rev", "ro") {
    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            tooFewArguments(atLeast = 1, actual = 0)
        }
        for (arg in args) {
            githw.revert(Paths.get(arg))
        }
    }
}
