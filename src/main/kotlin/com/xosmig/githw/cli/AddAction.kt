package com.xosmig.githw.cli

import com.xosmig.githw.commands.add
import java.nio.file.Paths

internal class AddAction : Action("Add file contents to the index", "add") {
    override fun run(args: List<String>) {
        when (args.size) {
            0 -> {
                println("TODO: 6")
                System.exit(2)
            }
            1 -> add(Paths.get(""), Paths.get(args[0]))
            else -> {
                println("TODO: 7")
                System.exit(2)
            }
        }
    }
}

