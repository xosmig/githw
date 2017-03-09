import com.xosmig.githw.commands.commit
import com.xosmig.githw.commands.init
import java.nio.file.Paths

val APP_NAME = "githw"
val MAX_COMMAND_LENGTH = 10

/**
 * Represent console sub-command such as "help" and "init".
 */
private abstract class Command(val description: String) {
    /**
     * Execute the commands with given arguments.
     */
    abstract fun run(args: List<String>)
}

private class HelpCommand : Command("Show this message") {
    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            println("usage: $APP_NAME <command> [<args>]")
            println()
            println("Commands:")
            for ((key, command) in COMMANDS) {
                println(String.format(" %-${MAX_COMMAND_LENGTH}s\t${command.description}", key))
            }
            println()
        }
    }
}

private class InitCommand: Command("Create an empty repository") {
    override fun run(args: List<String>) {
        when (args.size) {
            0 -> init(Paths.get(""))
            1 -> init(Paths.get(args[0]))
            else -> {
                println("TODO: too many arguments for githw init")
                System.exit(2)
            }
        }
    }
}

private class CommitCommand: Command("Record changes to the repository") {
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

private val COMMANDS = hashMapOf(
        "help" to HelpCommand(),
        "init" to InitCommand(),
        "commit" to CommitCommand()
)

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("see $APP_NAME help")
        return
    }

    val commandName = args[0]
    val command = COMMANDS[commandName]
    if (command != null) {
        command.run(args.asList().subList(1, args.size))
    } else {
        println("$APP_NAME: '$commandName' is not a valid command. See '$APP_NAME help'")
        System.exit(2)
    }
}
