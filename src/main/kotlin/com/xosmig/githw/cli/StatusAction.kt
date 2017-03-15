package com.xosmig.githw.cli

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.refs.Head
import java.nio.file.Paths

internal class StatusAction : Action("Show the working tree status", "status", "stat", "st") {
    override fun run(args: List<String>) {
        if (args.isNotEmpty()) {
            throw tooManyArguments(atMost = 0, actual = args.size)
        }
        val root = Paths.get("")
        val gitDir = root.resolve(GIT_DIR_PATH)
        val head = Head.load(gitDir)
        println(head)

        val untracked = githw.getUntrackedFiles(root)
        if (untracked.isNotEmpty()) {
            println("Untracked files:")
            for (file in untracked) {
                println("\t$file") // TODO: red color
            }
        }





    }
}

