package com.xosmig.githw

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.xosmig.githw.controller.GithwController
import com.xosmig.githw.utils.FilesUtils.copyRecursive
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files.*

class ExcludeTest: GithwTestClass() {

    @Test
    fun excludeGitDirTest() {
        val exclude = Exclude.loadFromRoot(root)
        assertTrue(exclude.contains(root.relativize(root.resolve(GIT_DIR_PATH))))
        assertFalse(exclude.contains(root.relativize(root.resolve("foo").resolve(GIT_DIR_PATH))))
    }

    @Test
    fun excludeSimpleTest() {
        githw.addExclude("foo", "bar")

        createFile(root.resolve("bar"))
        createFile(root.resolve("qwe"))
        createDirectories(root.resolve("foo/baz"))
        createFile(root.resolve("foo/baz/file.txt"))
        createDirectories(root.resolve("hello"))
        createFile(root.resolve("hello/foo"))

        githw.add(root)
        githw.commit("init")

        val newRoot = fs.getPath("/new/$rootDirName")
        createDirectories(newRoot)
        copyRecursive(root.resolve(GIT_DIR_PATH), newRoot.resolve(GIT_DIR_PATH))
        val githwCopy = GithwController(newRoot)
        githwCopy.revert(newRoot)

        assertFalse(exists(newRoot.resolve("bar")))
        assertTrue(exists(newRoot.resolve("qwe")))
        assertFalse(exists(newRoot.resolve("foo")))
        assertTrue(exists(newRoot.resolve("hello")))
        assertTrue(exists(newRoot.resolve("hello/foo")))
    }
}
