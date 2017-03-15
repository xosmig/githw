package com.xosmig.githw.cli

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException

internal class CommitAction : Action("Record changes to the repository", "commit", "cm") {
    override fun run(args: List<String>) {
        val options = Options()

        val messageOpt = Option("m", "message", true, "Use the given parameter as the commit message")
        messageOpt.isRequired = true
        options.addOption(messageOpt)

        val authorOpt = Option("a", "author", true, "Set the author of the commit")
        options.addOption(authorOpt)

        val parser = DefaultParser()

        val line = try {
            parser.parse(options, args.toTypedArray())
        } catch(e: ParseException) {
            fail("Parsing failed. Reason: ${e.message}")
        }

        if (line.hasOption(authorOpt.opt)) {
            githw.commit(line.getOptionValue(messageOpt.opt), author = line.getOptionValue(authorOpt.opt))
        } else {
            githw.commit(line.getOptionValue(messageOpt.opt))
        }
    }
}
