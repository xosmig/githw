
package com.xosmig.githw.cli

import com.xosmig.githw.APP_NAME
import com.xosmig.githw.controller.GithwController
import java.nio.file.Paths

/**
 * Represent console sub-command such as "help" and "init".
 */
internal abstract class Action(val description: String, val primaryName: String, vararg aliases: String) {

    val githw = GithwController(Paths.get(""))

    /** Aliases can be used instead of [primaryName] to call a sub-command from console. */
    val aliases: Set<String> = setOf(*aliases)

    /**
     * Execute the commands with given arguments.
     */
    abstract fun run(args: List<String>)

    /**
     * Creates messages for errors which happened during action processing.
     */
    private fun formatFail(message: String): String = "$APP_NAME $primaryName: $message"

    /**
     * Fail with the given message.
     */
    protected fun fail(message: String): Nothing = cliFail(formatFail(message))

    /**
     * Fail with the given message and exitCode.
     */
    protected fun fail(message: String, exitCode: Int): Nothing = cliFail(formatFail(message), exitCode)

    /**
     * Fail with a message that too many arguments were passed to the action.
     */
    fun tooManyArguments(atMost: Int, actual: Int): Nothing {
        fail("Too many arguments. Expected at most $atMost, actual: $actual")
    }

    /**
     * Fail with a message that too few arguments were passed to the action.
     */
    fun tooFewArguments(atLeast: Int, actual: Int): Nothing {
        fail("Too few arguments. Expected at least $atLeast, actual: $actual")
    }

    /**
     * Check whether or not the given name is an alias for the action.
     */
    fun hasName(name: String): Boolean = name == primaryName || aliases.contains(name)

    /**
     * Format action's [primaryName] and [comment] to print within a list.
     */
    fun formatWithComment(comment: String): String {
        return String.format(" %-${MAX_COMMAND_LENGTH}s\t%s", primaryName, comment)
    }

    /**
     * Print help message for the action.
     */
    open fun printUsage() = println("TODO: help message for '$primaryName'")
}
