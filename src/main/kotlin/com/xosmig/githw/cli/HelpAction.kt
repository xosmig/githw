package com.xosmig.githw.cli

internal class HelpAction : Action("Show this message", "help", "h", "-h", "-help", "--help") {
    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            println("usage: $APP_NAME <command> <args>")
            println()
            println("Commands:")
            for (action in ACTIONS) {
                println(action.formatWithComment(action.description))
            }
            println()
        }
    }
}
