package com.xosmig.githw.cli

import java.nio.file.Paths

/**
 * Action to restore working tree files.
 */
internal class RestoreAction : ActionInitialized("Restore working tree files", "restore", "res", "rs") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, atLeast = 1)

        if (args[0] == "--all" || args[0] == "-a") {
            githw.restoreAll()
        }

        for (arg in args) {
            githw.restore(Paths.get(arg))
        }
    }
}
