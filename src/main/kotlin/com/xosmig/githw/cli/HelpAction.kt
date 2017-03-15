package com.xosmig.githw.cli

import com.xosmig.githw.APP_NAME

internal class HelpAction : Action("Show this message", "help", "h", "-h", "-help", "--help") {
    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            printGeneralHelp()
            return
        }
        if (args.size > 1) {
            tooManyArguments(atMost = 1, actual = args.size)
        }
        val actionName = args[0]
        getActionByName(actionName)?.printUsage()
            ?: fail("Unknown command: '$APP_NAME $actionName'")
    }

    private fun printGeneralHelp() {
        println("Usage: $APP_NAME <command> <args>")
        println()
        println("Commands:")
        println()
        for (group in ACTION_GROUPS) {
            group.print()
            println()
        }
        println("See '$APP_NAME help <command>' to read about a specific sub-command")
    }
}
