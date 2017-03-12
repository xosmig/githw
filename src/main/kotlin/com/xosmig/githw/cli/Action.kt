
package com.xosmig.githw.cli

/**
 * Represent console sub-command such as "help" and "init".
 */
internal abstract class Action(val description: String, val primaryName: String, vararg aliases: String) {
    val aliases: Set<String> = setOf(*aliases)

    /**
     * Execute the commands with given arguments.
     */
    abstract fun run(args: List<String>)

    private fun formatFail(message: String): String = "$APP_NAME $primaryName: $message"

    protected fun fail(message: String): Nothing = cliFail(formatFail(message))

    protected fun fail(message: String, exitCode: Int): Nothing = cliFail(formatFail(message), exitCode)

    fun tooManyArguments(expected: Int, actual: Int) {
        cliFail("""
            |Too many arguments for command '$APP_NAME $primaryName'.
            |Expected: $expected, actual: $actual"
        """)
    }

    fun hasName(name: String): Boolean = name == primaryName || aliases.contains(name)

    fun formatWithComment(comment: String): String {
        return String.format(" %-${MAX_COMMAND_LENGTH}s\t%s", primaryName, comment)
    }

    // TODO: make abstract
    open fun printUsage() = println("TODO: help message for '$primaryName'")
}
