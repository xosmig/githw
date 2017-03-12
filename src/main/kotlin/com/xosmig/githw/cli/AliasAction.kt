package com.xosmig.githw.cli

/**
 * Shows aliases for all commands.
 */
internal class AliasAction : Action("Show aliases for all commands", "alias", "aliases") {
    override fun run(args: List<String>) {
        if (args.isNotEmpty()) {
            tooManyArguments(atMost = 0, actual = args.size)
        }
        println("Aliases can be used instead of primary name to call a sub-command from console")
        ACTIONS
                .filter { it.aliases.isNotEmpty() }
                .forEach { println(it.formatWithComment(it.aliases.toString().drop(1).dropLast(1))) }
    }
}
