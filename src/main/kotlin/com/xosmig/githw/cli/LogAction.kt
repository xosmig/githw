package com.xosmig.githw.cli

import com.xosmig.githw.controller.GithwController

/**
 * Action to show commit log.
 */
internal class LogAction(customController: GithwController? = null) :
        ActionInitialized(customController, "Show commit log", "log") {
    override fun run(args: List<String>) {
        checkArgNumber(exact = 0, actual = args.size)
        val log = githw.getLog()
        for (commit in log) {
            println(commit)
        }
    }
}
