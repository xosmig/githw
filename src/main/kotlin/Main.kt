import org.apache.commons.cli.Options

val APP_NAME = "githw"

private abstract class Command(val description: String) {
    abstract fun run(args: List<String>)
}

private class HelpCommand : Command("Show this message") {
    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            println("usage: $APP_NAME <command> [<args>]")
            println()
            println("Commands:")
            for ((key, command) in COMMANDS) {
                println(" $key \t\t ${command.description}")
            }
            println()
        }
    }
}

private class InitCommand : Command("Create an empty repository") {
    override fun run(args: List<String>) {
        // TODO
        throw UnsupportedOperationException("not implemented")
    }
}

private val COMMANDS = hashMapOf(
        "help" to HelpCommand(),
        "init" to InitCommand()
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
    }
}
