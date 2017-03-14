package com.xosmig.githw.cli

import com.xosmig.githw.commands.revert
import java.nio.file.Paths

internal class RevertAction : Action("Restore working tree files", "revert", "rev", "ro") {
    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            tooFewArguments(atLeast = 1, actual = 0)
        }
        for (arg in args) {
            revert(Paths.get(""), Paths.get(arg))
        }
    }
}
