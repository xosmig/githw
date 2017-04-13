package com.xosmig.githw.cli

import com.xosmig.githw.controller.GithwController
import java.nio.file.Paths

/**
 * Action to remove untracked files from a working tree.
 */
internal class CleanAction(customController: GithwController? = null) :
        ActionInitialized(customController, "Remove untracked files from the working tree", "clean") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, exact = 1)

        if (args[0] == "--all") {
            githw.clean(githw.root)
        } else {
            githw.clean(Paths.get(args[0]))
        }
    }
}
