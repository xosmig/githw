package com.xosmig.githw.commands

import com.xosmig.githw.GithwTestClass
import com.xosmig.githw.utils.toList
import org.junit.Test
import org.junit.Assert.*
import java.nio.file.Files.*
import java.nio.file.Path
import java.util.*
import java.util.stream.Collectors

class CleanTest : GithwTestClass() {

    @Test
    fun cleanSubdirTest() {
        randomUtils.randomDirectory(root, allowEmptyDirectories = true)
        val dir = root.resolve("foo bar")
        runTest(dir)
    }

    @Test
    fun cleanRootTest() {
        runTest(root)
    }

    fun runTest(dir: Path, commitProbability: Double = 0.2, indexProbability: Double = 0.2) {
        randomUtils.randomDirectory(dir, allowEmptyDirectories = true)
        val filesToStay = HashSet<Path>()

        // ignored files shouldn't be removed
        githw.addToIgnore(".*[0-9]")  // ignore all files and dirs which end with a digit
        val exclude = githw.ignore
        for (path in walk(dir)) {
            if (exclude.contains(path)) {
                filesToStay.addAll(walk(path).filter { isRegularFile(it) }.toList())
            }
        }

        // tracked files shouldn't be removed
        for (path in walk(dir).filter { !it.startsWith(gitDir) }) {
            if (isRegularFile(path) && randomUtils.nextBoolean(commitProbability)) {
                githw.add(path)
                filesToStay.add(path)
            }
        }
        githw.commit("foo")

        // files in d shouldn't be removed
        for (path in walk(dir).filter { !it.startsWith(gitDir) }) {
            if (isRegularFile(path) && randomUtils.nextBoolean(indexProbability)) {
                githw.add(path)
                filesToStay.add(path)
            }
        }

        // files in `gitDir` folder shouldn't be removed
        filesToStay.addAll(walk(dir).filter { it.startsWith(gitDir) && isRegularFile(it) }.toList())

        githw.clean(dir)
        val remainingFiles = walk(dir).filter { isRegularFile(it) }.collect(Collectors.toSet())

        assertEquals(emptySet<Path>(), filesToStay.minus(remainingFiles))
        assertEquals(emptySet<Path>(), remainingFiles.minus(filesToStay))
    }
}
