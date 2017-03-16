package com.xosmig.githw.cli

import com.xosmig.githw.APP_NAME
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException

/**
 * Action to join two branches together
 */
internal class MergeAction : Action("Join two branches together", "merge") {
    override fun run(args: List<String>) {
        checkArgNumber(args.size, atLeast = 1)

        val options = Options()

        val message = Option("m", "message", true, "Use the given parameter as the commit message")
        options.addOption(message)

        val author = Option("a", "author", true, "Set the author of the merge-commit")
        options.addOption(author)

        val failOnConflictOpt = Option(null, "fail-on-conflict", false, "Stop merge if there are any conflicts")
        options.addOption(failOnConflictOpt)

        val parser = DefaultParser()

        val line = try {
            parser.parse(options, args.toTypedArray())
        } catch(e: ParseException) {
            fail("Parsing failed. Reason: ${e.message}")
        }

        val otherBranchName = args[0]
        val failOnConflict = line.hasOption(failOnConflictOpt.opt)

        val newFiles = githw.merge(otherBranchName, line.getOptionValue(message.opt),
                line.getOptionValue(author.opt), failOnConflict)
        if (failOnConflict && newFiles.isNotEmpty()) {
            fail("Merge failed. See '$APP_NAME status' to see the list of conflicts")
        }

        println("Successfully merged with $otherBranchName")
        if (newFiles.isNotEmpty()) {
            println("Some new files created due to conflicts")
            for (file in newFiles) {
                println("\t$file")
            }
        }
    }
}

