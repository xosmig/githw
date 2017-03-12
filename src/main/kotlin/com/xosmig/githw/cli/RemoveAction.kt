package com.xosmig.githw.cli

import com.xosmig.githw.commands.remove
import java.nio.file.Paths

internal class RemoveAction : Action("Remove files from the working tree", "remove", "rm") {
    override fun run(args: List<String>) {
        when (args.size) {
            0 -> {
                println("TODO: 4")
                System.exit(2)
            }
            1 -> remove(Paths.get(""), Paths.get(args[0]))
            else -> {
                println("TODO: 5")
                System.exit(2)
            }
        }
    }
}
