package com.xosmig.githw.commands

import java.nio.file.Files.*
import java.nio.file.Path

/**
 * Remove all files which are not tracked nor ignored.
 */
fun gitClean(root: Path, path: Path) {
    getUntrackedFiles(root, path).forEach(::delete)
}
