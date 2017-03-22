package com.xosmig.githw.cli

import com.xosmig.githw.APP_NAME
import com.xosmig.githw.controller.BasicGithwController
import com.xosmig.githw.controller.BasicGithwController.Companion.isInitializedIn
import java.nio.file.Paths

internal abstract class ActionInitialized(description: String, primaryName: String, vararg aliases: String):
        Action(description, primaryName, *aliases) {

    val githw by lazy {
        var current = Paths.get("").toAbsolutePath()
        while (current != null && !isInitializedIn(current)) {
            current = current.parent
        }
        if (current == null) {
            fail("${Paths.get("").toAbsolutePath()} is not a valid $APP_NAME repository")
        }
        BasicGithwController(current)
    }
}
