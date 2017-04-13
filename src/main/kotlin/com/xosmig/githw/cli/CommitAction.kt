package com.xosmig.githw.cli

import com.xosmig.githw.controller.GithwController
import org.apache.commons.cli.*

/**
 * Action to record changes to a repository.
 */
internal class CommitAction(customController: GithwController? = null) :
        ActionInitialized(customController, "Record changes to the repository", "commit", "cm") {

    private val options = Options()
    private val messageOpt = Option("m", "message", true, "Use the given parameter as the commit message")
    private val authorOpt = Option("a", "author", true, "Set the author of the commit")

    init {
        messageOpt.isRequired = true
        options.addOption(messageOpt)
        options.addOption(authorOpt)
    }

    override fun run(args: List<String>) {
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
