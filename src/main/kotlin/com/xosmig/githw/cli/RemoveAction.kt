package com.xosmig.githw.cli

import java.nio.file.Paths

internal class RemoveAction : Action("Remove files from the working tree", "remove", "delete", "rm", "del") {
    override fun run(args: List<String>) {
        when (args.size) {
            0 -> {
                println("TODO: 4")
                System.exit(2)
            }
            1 -> githw.remove(Paths.get(args[0]))
            else -> {
                println("TODO: 5")
                System.exit(2)
            }
        }
    }
}
