package com.xosmig.githw.cli

import com.xosmig.githw.APP_NAME

/**
 * Maximum allowed length of a sub-command's name.
 * Used to format lists of commands.
 */
internal val MAX_COMMAND_LENGTH = 10

/**
 * The program will return this exitcode on fail there if there is no specified exitcode.
 */
internal val DEFAULT_FAIL_EXITCODE = 2

/**
 * Structure of this value represents help message's structure.
 *
 * Each available action must be in exactly one of these groups.
 */
internal val ACTION_GROUPS = listOf(
        ActionGroup (
                "information",
                HelpAction(),
                AliasAction(),
                StatusAction(),
                VersionAction(),
                LogAction()
        ),
        ActionGroup (
                "work on the current change",
                InitAction(),
                AddAction(),
                RemoveAction()
        ),
        ActionGroup (
                "reset changes in the working directory",
                CleanAction(),
                RestoreAction()
        ),
        ActionGroup (
                "work on history and branches",
                BranchAction(),
                CommitAction(),
                SwitchAction(),
                DetachAction(),
                MergeAction()
        )
)

/**
 * List of all available actions sorted by primary name.
 */
internal val ACTIONS = ACTION_GROUPS.flatMap { it.actions.asList() }.sortedBy { it.primaryName }

fun runApp(args: Array<String>) {
    if (args.isEmpty()) {
        cliFail("See $APP_NAME help")
    }

    val actionName = args[0]
    getActionByName(actionName)?.run(args.asList().subList(1, args.size))
        ?: cliFail("'$APP_NAME $actionName' is not a valid command. See '$APP_NAME help'")
}

/**
 * Get action by any of its names.
 */
internal fun getActionByName(actionName: String): Action? {
    return ACTIONS
            .filter { it.hasName(actionName) }
            .firstOrNull()
}

/**
 * Handle cli errors. Throws [CLIException].
 */
internal fun cliFail(message: String, exitCode: Int = DEFAULT_FAIL_EXITCODE): Nothing {
    throw CLIException(message, exitCode)
}

/**
 * Class for all exceptions connected with command line interface.
 *
 * @param[exitCode] recommendation about exit code in case of CLI error.
 */
open class CLIException internal constructor(message: String, val exitCode: Int): Exception(message)
