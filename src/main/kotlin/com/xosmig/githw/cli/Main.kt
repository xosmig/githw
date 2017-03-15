package com.xosmig.githw.cli

import com.xosmig.githw.APP_NAME

internal val MAX_COMMAND_LENGTH = 10
internal val DEFAULT_FAIL_EXITCODE = 2

internal val ACTIONS = listOf(
        HelpAction(),
        InitAction(),
        StatusAction(),
        CommitAction(),
        RemoveAction(),
        CleanAction(),
        AddAction(),
        RevertAction(),
        BranchAction(),
        AliasAction()
)

fun runApp(args: Array<String>) {
    if (args.isEmpty()) {
        cliFail("See $APP_NAME help")
    }

    val actionName = args[0]
    getActionByName(actionName)?.run(args.asList().subList(1, args.size))
        ?: cliFail("'$APP_NAME $actionName' is not a valid command. See '$APP_NAME help'")
}

internal fun getActionByName(actionName: String): Action? {
    return ACTIONS
            .filter { it.hasName(actionName) }
            .firstOrNull()
}

internal fun cliFail(message: String, exitCode: Int = DEFAULT_FAIL_EXITCODE): Nothing {
    throw CLIException(message, exitCode)
}

class CLIException internal constructor(message: String, val exitCode: Int): Exception(message)
