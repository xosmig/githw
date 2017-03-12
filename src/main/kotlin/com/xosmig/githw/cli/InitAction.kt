package com.xosmig.githw.cli

import com.xosmig.githw.commands.init
import java.nio.file.Paths

internal class InitAction : Action("Create an empty repository", "init", "ini") {
    override fun run(args: List<String>) {
        if (args.isNotEmpty()) {
            tooManyArguments(atMost = 0, actual = args.size)
        }
        init(Paths.get(""))
    }
}
