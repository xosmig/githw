package com.xosmig.githw.cli

/**
 * Action to switch branches.
 */
internal class SwitchAction : ActionInitialized("Switch branches", "switch", "sw") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, exact = 1)
        githw.switchBranch(args[0])
    }
}
