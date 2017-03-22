package com.xosmig.githw.cli

import com.xosmig.githw.controller.BasicGithwController
import java.nio.file.Paths

internal abstract class ActionInitialized(description: String, primaryName: String, vararg aliases: String):
        Action(description, primaryName, *aliases) {

    val githw by lazy { BasicGithwController(Paths.get("")) }
}
