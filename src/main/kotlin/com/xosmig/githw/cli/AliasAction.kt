package com.xosmig.githw.cli

internal class AliasAction : Action("Show aliases for all commands", "alias", "aliases") {
    override fun run(args: List<String>) {
        if (args.isNotEmpty()) {
            tooManyArguments(expected = 0, actual = args.size)
        }
        for (action in ACTIONS) {
            if (action.aliases.isNotEmpty()) {
                println(action.formatWithComment(action.aliases.toString().drop(1).dropLast(1)))
            }
        }
    }
}
