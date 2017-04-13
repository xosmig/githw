package com.xosmig.githw.cli

import com.xosmig.githw.controller.GithwController

/**
 * Action to switch branches.
 */
internal class SwitchAction(customController: GithwController? = null) :
        ActionInitialized(customController, "Switch branches", "switch", "sw") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, exact = 1)
        githw.switchBranch(args[0])
    }
}
