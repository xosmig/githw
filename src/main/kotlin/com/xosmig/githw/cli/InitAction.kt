package com.xosmig.githw.cli

/**
 * Action to create an empty repository
 */
internal class InitAction : Action("Create an empty repository", "init", "ini") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, exact = 0)
        githw.init()
    }
}
