package com.xosmig.githw.commands

import com.xosmig.githw.Exclude
import com.xosmig.githw.GithwTestClass
import com.xosmig.githw.index.Index
import com.xosmig.githw.utils.toList
import org.junit.Test
import org.junit.Assert.*
import java.nio.file.Files.*
import java.nio.file.Path
import java.util.*
import java.util.stream.Collectors

class CleanCommandTest: GithwTestClass() {

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
        Exclude.addToRoot(root, ".*[0-9]")  // exclude all files and dirs which end with a digit
        val exclude = Exclude.loadFromRoot(root)
        for (path in walk(dir)) {
            if (exclude.contains(path)) {
                filesToStay.addAll(walk(path).filter { isRegularFile(it) }.toList())
            }
        }

        // tracked files shouldn't be removed
        for (path in walk(dir).filter { !it.startsWith(gitDir) }) {
            if (isRegularFile(path) && randomUtils.nextBoolean(commitProbability)) {
                add(root, path)
                filesToStay.add(path)
            }
        }
        commit(root, "foo")

        // files in index shouldn't be removed
        for (path in walk(dir).filter { !it.startsWith(gitDir) }) {
            if (isRegularFile(path) && randomUtils.nextBoolean(indexProbability)) {
                add(root, path)
                filesToStay.add(path)
            }
        }

        // files in `gitDir` folder shouldn't be removed
        filesToStay.addAll(walk(dir).filter { it.startsWith(gitDir) && isRegularFile(it) }.toList())

        gitClean(root, dir)
        val remainingFiles = walk(dir).filter { isRegularFile(it) }.collect(Collectors.toSet())

        assertEquals(emptySet<Path>(), filesToStay.minus(remainingFiles))
        assertEquals(emptySet<Path>(), remainingFiles.minus(filesToStay))
    }
}
