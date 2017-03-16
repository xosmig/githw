import com.xosmig.githw.cli.CLIException
import com.xosmig.githw.cli.runApp

val ILLEGAL_ARGUMENT_EXIT_CODE = 17
val ILLEGAL_STATE_EXIT_CODE = 23
val NOT_IMPLEMENTED_EXIT_CODE = 67

fun main(args: Array<String>) {
    try {
        runApp(args)
    } catch (e: CLIException) {
        print(e.message)
        if (e.message!!.last() != '\n') {
            println()
        }
        System.exit(e.exitCode)
    } catch (e: IllegalArgumentException) {
        println("illegal argument: ${e.message}")
        System.exit(ILLEGAL_ARGUMENT_EXIT_CODE)
    } catch (e: IllegalStateException) {
        println("oops, bad repository state: ${e.message}")
        System.exit(ILLEGAL_STATE_EXIT_CODE)
    } catch (e: NotImplementedError) {
        println("oops, this feature is not implemented yet. ${e.message}")
        System.exit(NOT_IMPLEMENTED_EXIT_CODE)
    }
}
