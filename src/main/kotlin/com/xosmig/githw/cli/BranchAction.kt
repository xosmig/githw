package com.xosmig.githw.cli

internal class BranchAction : Action("List, create, or delete branches", "branch", "br") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, atMost = 1)

        if (args.isEmpty() || args[0] == "--show") {
            githw.getBranches().forEach(::println)
        } else {
            githw.newBranch(args[0])
        }
    }
}
