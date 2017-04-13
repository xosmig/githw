package com.xosmig.githw.cli

import com.xosmig.githw.controller.BasicGithwController
import com.xosmig.githw.controller.GithwController
import java.nio.file.Paths

internal abstract class ActionInitialized( customController: GithwController?,
                                           description: String, primaryName: String,
                                           vararg aliases: String): Action(description, primaryName, *aliases) {

    companion object {
        val defaultController by lazy { BasicGithwController.openRecursive(Paths.get("")) }
    }

    val githw by lazy { customController ?: defaultController }
}
