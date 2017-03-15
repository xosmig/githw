package com.xosmig.githw.cli

internal class ActionGroup(val description: String, vararg val actions: Action) {
    fun print() {
        println(description)
        for (action in actions) {
            println(action.formatWithComment(action.description))
        }
    }
}
