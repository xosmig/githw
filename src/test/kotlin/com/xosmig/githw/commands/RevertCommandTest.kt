package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.GithwTestClass
import org.junit.Assert.*
import org.junit.Test
import com.xosmig.githw.testutils.getSha256
import com.xosmig.githw.utils.FilesUtils.copyRecursive
import java.nio.file.Files.*

class RevertCommandTest: GithwTestClass() {

    @Test
    fun revertSingleRemovedFile() {
        val filePath = root.resolve("file.txt")
        val content = "Hello, World".toByteArray()
        newOutputStream(filePath).use {
            it.write(content)
        }
        add(root, filePath)
        commit(root, "test: file '$filePath' added")

        delete(filePath)
        revert(root, filePath)
        assertArrayEquals(content, readAllBytes(filePath))
    }

    @Test
    fun revertEmptyRootDirectory() {
        randomUtils.randomDirectory(root)
        add(root, root)
        commit(root, "test: random directory created.")
        val sha256 = getSha256(root)

        val newRoot = fs.getPath("/new/$rootDirName")
        createDirectories(newRoot)
        copyRecursive(root.resolve(GIT_DIR_PATH), newRoot.resolve(GIT_DIR_PATH))
        revert(newRoot, newRoot)

        assertEquals(sha256, getSha256(newRoot))
    }
}
