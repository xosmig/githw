package com.xosmig.githw.cli

import com.xosmig.githw.APP_NAME

/**
 * Action to show help message.
 */
internal class HelpAction : Action("Show this message", "help", "h", "-h", "-help", "--help") {
    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            printGeneralHelp()
            return
        }
        checkArgNumber(args.size, atMost = 1)
        val actionName = args[0]
        getActionByName(actionName)?.printHelp()
            ?: fail("Unknown command: '$APP_NAME $actionName'")
    }

    private fun printGeneralHelp() {
        println("Usage: $APP_NAME <command> <args>")
        println()
        println("Commands:")
        println()
        for (group in ACTION_GROUPS) {
            group.printWithComments { it.description }
            println()
        }
        println("See '$APP_NAME help <command>' to read about a specific sub-command")
    }
}
