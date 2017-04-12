package com.xosmig.githw.cli

import java.nio.file.Paths

/**
 * Action to reset working tree files.
 */
internal class ResetAction : ActionInitialized("Reset working tree files", "reset", "res", "rs") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, atLeast = 1)

        if (args[0] == "--all" || args[0] == "-a") {
            githw.resetAll()
        }

        for (arg in args) {
            githw.reset(Paths.get(arg))
        }
    }
}
