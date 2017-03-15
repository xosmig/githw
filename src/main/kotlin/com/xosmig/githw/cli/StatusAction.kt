package com.xosmig.githw.cli

internal class StatusAction : Action("Show the working tree status", "status", "stat", "st") {
    override fun run(args: List<String>) {
        if (args.isNotEmpty()) {
            throw tooManyArguments(atMost = 0, actual = args.size)
        }
        TODO("not implemented")
    }
}

