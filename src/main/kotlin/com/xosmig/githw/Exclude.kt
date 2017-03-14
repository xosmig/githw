package com.xosmig.githw

import com.fulmicoton.multiregexp.MultiPattern
import java.nio.file.Files.*
import java.nio.file.Path
import java.util.stream.Collectors

class Exclude private constructor(templates: List<String>) {

    private val matcher = MultiPattern.of(templates).matcher()

    companion object {
        val GIT_DIR_PATTERN = GIT_DIR_PATH.replace(".", "\\.")

        private fun loadListFromFile(file: Path) = lines(file)
                .collect(Collectors.toList())

        fun loadFromRoot(root: Path): Exclude {
            val templates = loadListFromFile(root.resolve(GIT_DIR_PATH).resolve(EXCLUDE_PATH))
            if (exists(root.resolve(IGNORE_PATH))) {
                templates.addAll(loadListFromFile(root.resolve(IGNORE_PATH)))
            }
            templates.add(GIT_DIR_PATTERN)
            return Exclude(templates)
        }
    }

    fun contains(path: Path): Boolean = matcher.match(path.normalize().toString()).isNotEmpty()
}
