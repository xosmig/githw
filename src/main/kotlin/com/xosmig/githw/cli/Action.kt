
package com.xosmig.githw.cli

import com.xosmig.githw.APP_NAME
import com.xosmig.githw.controller.BasicGithwController
import java.nio.file.Paths

/**
 * Represent console sub-command such as "help" and "init".
 */
internal abstract class Action(val description: String, val primaryName: String, vararg aliases: String) {

    /** Aliases can be used instead of [primaryName] to call a sub-command from console. */
    val aliases: Set<String> = setOf(*aliases)

    /**
     * Execute the commands with the given arguments.
     *
     * @param[args] command line arguments for the sub-command (must not include sub-command name itself)
     *
     * @throws IllegalArgumentException in case of attempt to do something illegal with the repository
     * @throws IllegalStateException in case of damaged or broken repository or internal assertion fault
     */
    abstract fun run(args: List<String>)

    /**
     * Creates messages for errors which happened during action processing.
     */
    private fun formatFail(message: String): String = "$APP_NAME $primaryName: $message\n" +
                                                        "See '$APP_NAME help $primaryName' for more information"

    /**
     * Fail with the given message.
     */
    protected fun fail(message: String): Nothing = cliFail(formatFail(message))

    /**
     * Fail with the given message and exitCode.
     */
    protected fun fail(message: String, exitCode: Int): Nothing = cliFail(formatFail(message), exitCode)

    protected fun checkArgNumber(actual: Int, atLeast: Int = 0, atMost: Int = 999, exact: Int? = null) {
        if (exact != null && exact != actual) {
            fail("Wrong number of arguments. Expected $exact, actual: $actual")
        }
        if (actual < atLeast) {
            fail("Too few arguments. Expected at least $atLeast, actual: $actual")
        }
        if (actual > atMost) {
            fail("Too many arguments. Expected at most $atMost, actual: $actual")
        }
    }

    /**
     * Check whether or not the given name is an alias for the action.
     */
    fun hasName(name: String): Boolean = name == primaryName || aliases.contains(name)

    /**
     * Format action's [primaryName] and [comment] to print within a list.
     */
    fun formatWithComment(comment: String): String {
        return String.format("   %-${MAX_COMMAND_LENGTH}s\t%s", primaryName, comment)
    }

    /**
     * Print help message for the action.
     */
    open fun printHelp() = println("TODO: help message for '$primaryName'")
}
