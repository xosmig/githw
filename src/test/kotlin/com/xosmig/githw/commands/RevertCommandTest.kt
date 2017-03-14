package com.xosmig.githw.commands

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.xosmig.githw.GIT_DIR_PATH
import org.junit.Assert.*
import org.junit.Test
import com.xosmig.githw.testutils.RandomObjects
import com.xosmig.githw.testutils.getSha256
import com.xosmig.githw.utils.FilesUtils.copyRecursive
import java.nio.file.Files.*

class RevertCommandTest {

    @Test
    fun revertSingleRemovedFile() {
        val fs = Jimfs.newFileSystem(Configuration.unix())!!
        val root = fs.getPath("/projectRoot")
        createDirectories(root)
        init(root)

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
        val fs = Jimfs.newFileSystem(Configuration.unix())!!
        val rootDirName = "projectRoot"

        val root = fs.getPath("/$rootDirName")
        createDirectories(root)
        init(root)
        val rnd = RandomObjects()
        rnd.randomDirectory(root)
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
