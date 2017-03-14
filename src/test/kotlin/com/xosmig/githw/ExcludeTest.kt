package com.xosmig.githw

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.xosmig.githw.commands.*
import com.xosmig.githw.utils.FilesUtils.copyRecursive
import org.junit.Assert.*
import org.junit.Test
import java.io.PrintWriter
import java.nio.file.Files.*

class ExcludeTest {

    @Test
    fun excludeGitDirTest() {
        val fs = Jimfs.newFileSystem(Configuration.unix())!!
        val rootDirName = "projectRoot"

        val root = fs.getPath("/$rootDirName")
        createDirectories(root)
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
        createDirectories(root)
        init(root)
        newOutputStream(root.resolve(IGNORE_PATH)).use {
            PrintWriter(it).use {
                it.println("foo")
                it.println("bar")
            }
        }

        createFile(root.resolve("bar"))
        createFile(root.resolve("qwe"))
        createDirectories(root.resolve("foo/baz"))
        createFile(root.resolve("foo/baz/file.txt"))
        createDirectories(root.resolve("hello"))
        createFile(root.resolve("hello/foo"))

        add(root = root, path = root)
        commit(root, "init")

        val newRoot = fs.getPath("/new/$rootDirName")
        createDirectories(newRoot)
        copyRecursive(root.resolve(GIT_DIR_PATH), newRoot.resolve(GIT_DIR_PATH))
        revert(root = newRoot, path = newRoot)

        assertFalse(exists(newRoot.resolve("bar")))
        assertTrue(exists(newRoot.resolve("qwe")))
        assertFalse(exists(newRoot.resolve("foo")))
        assertTrue(exists(newRoot.resolve("hello")))
        assertTrue(exists(newRoot.resolve("hello/foo")))
    }
}
