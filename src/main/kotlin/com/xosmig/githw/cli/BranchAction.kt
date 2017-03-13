package com.xosmig.githw.cli

import com.xosmig.githw.commands.getBranches
import com.xosmig.githw.commands.newBranch
import java.nio.file.Paths

internal class BranchAction : Action("List, create, or delete branches", "branch", "br") {
    override fun run(args: List<String>) {
        val root = Paths.get("")
        when (args.size) {
            0 -> getBranches(root)
            1 -> newBranch(root, args[0])
            else -> tooManyArguments(atMost = 1, actual = args.size)
        }
    }
}
