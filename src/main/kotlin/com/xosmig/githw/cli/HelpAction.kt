package com.xosmig.githw.cli

internal class HelpAction : Action("Show this message", "help", "h", "-h", "-help", "--help") {
    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            printGeneralHelp()
            return
        }
        if (args.size > 1) {
            tooManyArguments(expected = 1, actual = args.size)
        }
        val actionName = args[0]
        getActionByName(actionName)?.printUsage()
            ?: fail("Unknown command: '$APP_NAME $actionName'")
    }

    private fun printGeneralHelp() {
        println("Usage: $APP_NAME <command> <args>")
        println()
        println("Commands:")
        for (action in ACTIONS) {
            println(action.formatWithComment(action.description))
        }
        println()
    }
}
