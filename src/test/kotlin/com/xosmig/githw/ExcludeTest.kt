package com.xosmig.githw

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.xosmig.githw.commands.*
import com.xosmig.githw.testutils.copy
import org.junit.Assert.*
import org.junit.Test
import java.io.PrintWriter
import java.nio.file.Files

class ExcludeTest {

    @Test
    fun excludeGitDirTest() {
        val fs = Jimfs.newFileSystem(Configuration.unix())!!
        val rootDirName = "projectRoot"

        val root = fs.getPath("/$rootDirName")
        Files.createDirectories(root)
        init(root)

        val exclude = Exclude.loadFromRoot(root)
        assertTrue(exclude.contains(root.relativize(root.resolve(GIT_DIR_PATH))))
        assertFalse(exclude.contains(root.relativize(root.resolve("foo").resolve(GIT_DIR_PATH))))
    }

    @Test
    fun excludeSimpleTest() {
        val fs = Jimfs.newFileSystem(Configuration.unix())!!
        val rootDirName = "projectRoot"

        val root = fs.getPath("/$rootDirName")
        Files.createDirectories(root)
        init(root)
        Files.newOutputStream(root.resolve(IGNORE_PATH)).use {
            PrintWriter(it).use {
                it.println("foo")
                it.println("bar")
            }
        }

        Files.createFile(root.resolve("bar"))
        Files.createFile(root.resolve("qwe"))
        Files.createDirectories(root.resolve("foo/baz"))
        Files.createFile(root.resolve("foo/baz/file.txt"))
        Files.createDirectories(root.resolve("hello"))
        Files.createFile(root.resolve("hello/foo"))

        add(root = root, path = root)
        commit(root, "init")

        val newRoot = fs.getPath("/new/$rootDirName")
        Files.createDirectories(newRoot)
        copy(root.resolve(GIT_DIR_PATH), newRoot.resolve(GIT_DIR_PATH))
        revert(root = newRoot, path = newRoot)

        assertFalse(Files.exists(newRoot.resolve("bar")))
        assertTrue(Files.exists(newRoot.resolve("qwe")))
        assertFalse(Files.exists(newRoot.resolve("foo")))
        assertTrue(Files.exists(newRoot.resolve("hello")))
        assertTrue(Files.exists(newRoot.resolve("hello/foo")))
    }
}
