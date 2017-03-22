package com.xosmig.githw.cli

import com.xosmig.githw.controller.BasicGithwController
import java.nio.file.Paths

/**
 * Action to create an empty repository.
 */
internal class InitAction : Action("Create an empty repository", "init", "ini") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, exact = 0)
        BasicGithwController.init(Paths.get(""))
    }
}
