package com.xosmig.githw.cli

import com.xosmig.githw.utils.Sha256

internal class DetachAction : ActionInitialized("Detach HEAD", "detach") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, exact = 1)
        githw.detachHead(Sha256.fromString(args[0]))
    }
}
