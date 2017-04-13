package com.xosmig.githw.cli

import com.xosmig.githw.controller.GithwController
import com.xosmig.githw.utils.Sha256

internal class DetachAction(customController: GithwController? = null) :
        ActionInitialized(customController, "Detach HEAD", "detach") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, exact = 1)
        githw.detach(Sha256.fromString(args[0]))
    }
}
