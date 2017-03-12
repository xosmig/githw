package com.xosmig.githw.cli


internal val APP_NAME = "githw"
internal val MAX_COMMAND_LENGTH = 10
internal val DEFAULT_FAIL_EXITCODE = 2

internal val ACTIONS = listOf(
        HelpAction(),
        InitAction(),
        CommitAction(),
        RemoveAction(),
        AddAction(),
        CheckoutAction(),
        AliasAction()
)

fun runApp(args: Array<String>) {
    if (args.isEmpty()) {
        cliFail("See $APP_NAME help")
    }

    val actionName = args[0]
    for (action in ACTIONS) {
        if (action.hasName(actionName)) {
            action.run(args.asList().subList(1, args.size))
            return
        }
    }

    cliFail("$'APP_NAME $actionName' is not a valid command. See '$APP_NAME help'")
}

internal fun cliFail(message: String, exitCode: Int = DEFAULT_FAIL_EXITCODE): Nothing {
    throw CLIException(message, exitCode)
}

class CLIException internal constructor(message: String, val exitCode: Int): Exception(message)

