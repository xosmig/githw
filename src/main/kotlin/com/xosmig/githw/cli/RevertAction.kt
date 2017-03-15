package com.xosmig.githw.cli

import java.nio.file.Paths

internal class RevertAction : Action("Restore working tree files", "revert", "rev", "rv") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, atLeast = 1)
        for (arg in args) {
            githw.revert(Paths.get(arg))
        }
    }
}
