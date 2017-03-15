package com.xosmig.githw.cli

internal class InitAction : Action("Create an empty repository", "init", "ini") {
    override fun run(args: List<String>) {
        if (args.isNotEmpty()) {
            tooManyArguments(atMost = 0, actual = args.size)
        }
        githw.init()
    }
}
