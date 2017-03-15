package com.xosmig.githw.commands

import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.GithwTestClass
import com.xosmig.githw.controller.GithwController
import org.junit.Assert.*
import org.junit.Test
import com.xosmig.githw.utils.FilesUtils.copyRecursive
import com.xosmig.githw.utils.FilesUtils.countSha256
import java.nio.file.Files.*

class RevertCommandTest: GithwTestClass() {

    @Test
    fun revertSingleRemovedFile() {
        val filePath = root.resolve("file.txt")
        val content = "Hello, World".toByteArray()
        newOutputStream(filePath).use {
            it.write(content)
        }
        githw.add(filePath)
        githw.commit("test: file '$filePath' added")

        delete(filePath)
        githw.revert(filePath)
        assertArrayEquals(content, readAllBytes(filePath))
    }

    @Test
    fun revertEmptyRootDirectory() {
        randomUtils.randomDirectory(root)
        githw.add(root)
        githw.commit("test: random directory created.")
        val sha256 = countSha256(root)

        val newRoot = fs.getPath("/new/$rootDirName")
        createDirectories(newRoot)
        copyRecursive(root.resolve(GIT_DIR_PATH), newRoot.resolve(GIT_DIR_PATH))
        val newGithw = GithwController(newRoot)
        newGithw.revert(newRoot)

        assertEquals(sha256, countSha256(newRoot))
    }
}
