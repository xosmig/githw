
import com.xosmig.githw.commands.*
import java.nio.file.Paths

private val APP_NAME = "githw"
private val MAX_COMMAND_LENGTH = 10
private val DEFAULT_FAIL_EXITCODE = 2


/**
 * Represent console sub-command such as "help" and "init".
 */
private abstract class Action(val description: String, val primaryName: String, vararg aliases: String) {
    val aliases: Set<String> = setOf(*aliases)

    /**
     * Execute the commands with given arguments.
     */
    abstract fun run(args: List<String>)

    fun tooManyArguments(expected: Int, actual: Int) {
        fail("""
            |Too many arguments for command '$APP_NAME $primaryName'.
            |Expected: $expected, actual: $actual"
        """)
    }

    fun hasName(name: String): Boolean = name == primaryName || aliases.contains(name)

    fun formatWithComment(comment: String): String {
            return String.format(" %-${MAX_COMMAND_LENGTH}s\t%s", primaryName, comment)
    }
}

private class HelpAction : Action("Show this message", "help", "h", "-h", "-help", "--help") {
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

private class InitAction : Action("Create an empty repository", "init", "ini") {
    override fun run(args: List<String>) {
        if (args.isNotEmpty()) {
            tooManyArguments(expected = 0, actual = args.size)
        }
        init(Paths.get(""))
    }
}

private class CommitAction : Action("Record changes to the repository", "commit") {
    override fun run(args: List<String>) {
        when (args.size) {
            0 -> {
                println("TODO: 2")
                System.exit(2)
            }
            1 -> commit(Paths.get(""), args[0])
            else -> {
                println("TODO: 3")
                System.exit(2)
            }
        }
    }
}

private class RemoveAction : Action("Remove files from the working tree", "remove", "rm") {
    override fun run(args: List<String>) {
        when (args.size) {
            0 -> {
                println("TODO: 4")
                System.exit(2)
            }
            1 -> remove(Paths.get(""), Paths.get(args[0]))
            else -> {
                println("TODO: 5")
                System.exit(2)
            }
        }
    }
}

private class AddAction : Action("Add file contents to the index", "add") {
    override fun run(args: List<String>) {
        when (args.size) {
            0 -> {
                println("TODO: 6")
                System.exit(2)
            }
            1 -> add(Paths.get(""), Paths.get(args[0]))
            else -> {
                println("TODO: 7")
                System.exit(2)
            }
        }
    }
}

private class CheckoutAction : Action("Restore working tree files", "checkout", "co") {
    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            println("TODO: 12")
        }
        for (arg in args) {
            checkout(Paths.get(""), Paths.get(arg))
        }
    }
}

private class AliasAction : Action("Show aliases for all commands", "alias", "aliases") {
    override fun run(args: List<String>) {
        if (args.isNotEmpty()) {
            tooManyArguments(expected = 0, actual = args.size)
        }
        for (action in ACTIONS) {
            if (action.aliases.isNotEmpty()) {
                println(action.formatWithComment(action.aliases.toString().drop(1).dropLast(1)))
            }
        }
    }
}

private val ACTIONS = listOf(
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
        fail("See $APP_NAME help")
    }

    val actionName = args[0]
    for (action in ACTIONS) {
        if (action.hasName(actionName)) {
            action.run(args.asList().subList(1, args.size))
            return
        }
    }

    fail("$'APP_NAME $actionName' is not a valid command. See '$APP_NAME help'")
}

fun main(args: Array<String>) {
    try {
        runApp(args)
    } catch (e: CLIException) {
        print(e.message)
        if (e.message!!.last() != '\n') {
            println()
        }
        System.exit(e.exitCode)
    }
}

private fun fail(message: String = "", exitCode: Int = DEFAULT_FAIL_EXITCODE) {
    throw CLIException(message, exitCode)
}

private class CLIException(message: String, val exitCode: Int): Exception(message)
