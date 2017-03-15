package com.xosmig.githw.commands

import com.xosmig.githw.Exclude
import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.GithwTestClass
import com.xosmig.githw.utils.toList
import org.junit.Test
import org.junit.Assert.*
import java.nio.file.Files.isRegularFile
import java.nio.file.Files.walk
import java.nio.file.Path
import java.util.*
import java.util.stream.Collectors

class CleanCommandTest: GithwTestClass() {

    @Test(expected = IllegalArgumentException::class)
    fun cleanIndexIsNotEmptyTest() {
        randomUtils.randomDirectory(root, allowEmptyDirectories = true)
        walk(root).filter { isRegularFile(it) && randomUtils.nextBoolean(0.2) }
                .forEach { add(root, it) }
        gitClean(root, root)
    }

    @Test
    fun cleanRootTest() {
        randomUtils.randomDirectory(root, allowEmptyDirectories = true)
        Exclude.addToRoot(root, ".*[0-9]")  // exclude all files and dirs which end with a digit
        val exclude = Exclude.loadFromRoot(root)
        val filesToStay = HashSet<Path>()

        for (path in walk(root).filter { !it.startsWith(gitDir) }) {
            if (exclude.contains(path)) {
                filesToStay.addAll(walk(path).filter { isRegularFile(it) }.toList())
            }
            if (isRegularFile(path) && randomUtils.nextBoolean(0.2)) {
                add(root, path)
                filesToStay.add(path)
            }
        }
        commit(root, "init")
        filesToStay.addAll(walk(gitDir).filter { isRegularFile(it) }.toList())

        gitClean(root, root)
        val remainingFiles = walk(root).filter { isRegularFile(it) }.collect(Collectors.toSet())

        assertEquals(emptySet<Path>(), filesToStay.minus(remainingFiles))
        assertEquals(emptySet<Path>(), remainingFiles.minus(filesToStay))
    }
}
