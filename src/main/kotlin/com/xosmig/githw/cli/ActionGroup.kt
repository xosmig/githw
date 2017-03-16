package com.xosmig.githw.cli

/**
 * Represents a group of actions, united by some principle described in [description].
 */
internal class ActionGroup(val description: String, vararg val actions: Action) {
    fun print() {
        println(description)
        for (action in actions) {
            println(action.formatWithComment(action.description))
        }
    }
}
