package com.xosmig.githw.cli

import com.xosmig.githw.APP_NAME
import com.xosmig.githw.VERSION

internal class VersionAction : Action("Output version information", "version", "--version", "-version", "-v") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, exact = 0)
        println("$APP_NAME version $VERSION")
    }
}
