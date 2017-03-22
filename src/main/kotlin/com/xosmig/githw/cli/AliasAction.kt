package com.xosmig.githw.cli

/**
 * Action to show aliases for all controller.
 */
internal class AliasAction : Action("Show aliases for all controller", "alias", "aliases") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, exact = 0)

        println("Aliases can be used instead of primary name to call a sub-command from console")
        println()

        for (group in ACTION_GROUPS) {
            group.printWithComments {
                if (it.aliases.isEmpty()) {
                    ""
                } else {
                    val builder = StringBuilder()
                    builder.append(it.aliases.first())
                    for (alias in it.aliases.drop(1)) {
                        builder.append(", ")
                        builder.append(alias)
                    }
                    builder.toString()
                }
            }
            println()
        }
    }
}
