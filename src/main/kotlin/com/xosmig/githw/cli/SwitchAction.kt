package com.xosmig.githw.cli

internal class SwitchAction : Action("Switch branches", "switch", "sw") {
    override fun run(args: List<String>) {
        when (args.size) {
            0 -> tooFewArguments(atLeast = 1, actual = 0)
            1 -> githw.switchBranch(args[0])
            else -> tooManyArguments(atMost = 1, actual = args.size)
        }
    }
}
