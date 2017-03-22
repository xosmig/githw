package com.xosmig.githw.cli

/**
 * Action to show commit log.
 */
internal class LogAction : Action("Show commit log", "log") {
    override fun run(args: List<String>) {
        checkArgNumber(exact = 0, actual = args.size)
        val log = githw.getLog()
        for (commit in log) {
            println(commit)
        }
    }
}
