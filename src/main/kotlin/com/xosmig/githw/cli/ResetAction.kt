package com.xosmig.githw.cli

import com.xosmig.githw.controller.GithwController
import java.nio.file.Paths

/**
 * Action to reset working tree files.
 */
internal class ResetAction(customController: GithwController? = null) :
        ActionInitialized(customController, "Reset working tree files", "reset", "res", "rs") {
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
