package com.xosmig.githw.cli

import com.xosmig.githw.controller.GithwController
import java.nio.file.Paths

/**
 * Action to add files' content to the index.
 */
internal class AddAction(customController: GithwController? = null) :
        ActionInitialized(customController, "Add file's content to the index", "add") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, atLeast = 1)

        if (args[0] == "--all" || args[0] == "-a") {
            checkArgNumber(args.size, atMost = 1)
            githw.addAll()
            return
        }

        for (path in args) {
            githw.add(Paths.get(path))
        }
    }
}

