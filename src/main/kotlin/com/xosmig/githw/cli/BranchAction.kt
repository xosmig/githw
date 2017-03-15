package com.xosmig.githw.cli

import java.nio.file.Paths

internal class BranchAction : Action("List, create, or delete branches", "branch", "br") {
    override fun run(args: List<String>) {
        checkInitialized()

        val root = Paths.get("")
        when (args.size) {
            0 -> githw.getBranches().forEach(::println)
            1 -> githw.newBranch(args[0])
            else -> tooManyArguments(atMost = 1, actual = args.size)
        }
    }
}
