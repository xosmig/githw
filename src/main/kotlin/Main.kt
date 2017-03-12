import com.xosmig.githw.cli.CLIException
import com.xosmig.githw.cli.runApp

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
