package com.xosmig.githw.cli

/**
 * Action to show aliases for all commands.
 */
internal class AliasAction : Action("Show aliases for all commands", "alias", "aliases") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, exact = 0)

        println("Aliases can be used instead of primary name to call a sub-command from console")
        for (action in ACTIONS.filter { it.aliases.isNotEmpty() }) {
            println(action.formatWithComment(action.aliases.toString().drop(1).dropLast(1)))
        }
    }
}
