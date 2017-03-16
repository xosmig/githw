package com.xosmig.githw.cli

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException

/**
 * Action to list, create, or delete branches
 */
internal class BranchAction : Action("List, create, or delete branches", "branch", "br") {

    private val options = Options()
    private val deleteOpt = Option("d", "delete", true, "Delete a branch")
    private val listOpt = Option("l", "list", false, "Show the list of all branches")
    private val newOpt = Option("n", "new", true, "Create a new branch")

    init {
        options.addOption(deleteOpt)
        options.addOption(listOpt)
        options.addOption(newOpt)
    }

    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            showList()
        }

        val parser = DefaultParser()

        val line = try {
            parser.parse(options, args.toTypedArray())
        } catch(e: ParseException) {
            fail("Parsing failed. Reason: ${e.message}")
        }

        if (line.hasOption(deleteOpt.opt)) {
            checkArgNumber(actual = args.size, exact = 2)
            val branchName = line.getOptionValue(deleteOpt.opt)
            githw.deleteBranch(branchName)
            println("Branch '$branchName' successfully deleted")
        }

        if (line.hasOption(newOpt.opt)) {
            checkArgNumber(actual = args.size, exact = 2)
            val branchName = line.getOptionValue(newOpt.opt)
            githw.newBranch(branchName)
            println("Branch '$branchName' successfully created")
        }

        if (line.hasOption(listOpt.opt)) {
            checkArgNumber(actual = args.size, exact = 1)
            showList()
        }
    }

    private fun showList() {
        githw.getBranches().forEach(::println)
    }
}
