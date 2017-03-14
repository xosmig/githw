package com.xosmig.githw.utils

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.xosmig.githw.testutils.RandomUtils
import com.xosmig.githw.testutils.getSha256
import com.xosmig.githw.utils.FilesUtils.copyRecursive
import com.xosmig.githw.utils.FilesUtils.isEmptyDir
import org.junit.Test

import org.junit.Assert.*
import java.nio.file.Files.createDirectories
import java.nio.file.Files.createFile

class FilesUtilsTest {

    val fs = Jimfs.newFileSystem(Configuration.unix())!!
    val randomUtils = RandomUtils()

    @Test
    fun copyRecursiveFileTest() {
        val foo = fs.getPath("/foo")
        randomUtils.randomContent(foo)
        val sha256 = getSha256(foo)

        val bar = fs.getPath("/bar")
        copyRecursive(foo, bar)
        assertEquals(sha256, getSha256(bar))
    }

    @Test
    fun copyRecursiveDirectoryTest() {
        val foo = fs.getPath("/foo")
        randomUtils.randomDirectory(foo, allowEmptyDirectories = true)
        val sha256 = getSha256(foo)

        val bar = fs.getPath("/bar")
        copyRecursive(foo, bar)
        assertEquals(sha256, getSha256(bar))
    }

    @Test
    fun isEmptyDirFalseTest1() {
        val foo = fs.getPath("/foo")
        createFile(foo)
        assertFalse(isEmptyDir(foo))
    }

    @Test
    fun isEmptyDirFalseTest2() {
        val foo = fs.getPath("/foo")
        createDirectories(foo)
        createFile(foo.resolve("bar"))
        assertFalse(isEmptyDir(foo))
    }

    @Test
    fun isEmptyDirTrueTest() {
        val foo = fs.getPath("/foo")
        createDirectories(foo)
        assertTrue(isEmptyDir(foo))
    }

}
