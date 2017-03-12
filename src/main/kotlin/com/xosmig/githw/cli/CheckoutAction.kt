package com.xosmig.githw.cli

import com.xosmig.githw.commands.checkout
import java.nio.file.Paths

internal class CheckoutAction : Action("Restore working tree files", "checkout", "co") {
    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            println("TODO: 12")
        }
        for (arg in args) {
            checkout(Paths.get(""), Paths.get(arg))
        }
    }
}

