package com.xosmig.githw.cli

import com.xosmig.githw.controller.GithwController
import java.nio.file.Paths

/**
 * Action to remove files from a working tree.
 */
internal class RemoveAction(customController: GithwController? = null) :
        ActionInitialized(customController, "Remove files from the working tree", "remove", "delete", "rm", "del") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, atLeast = 1)
        for (path in args) {
            githw.remove(Paths.get(path))
        }
    }
}
