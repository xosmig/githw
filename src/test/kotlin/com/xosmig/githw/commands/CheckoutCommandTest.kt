package com.xosmig.githw.commands

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class CheckoutCommandTest {

    @Test
    fun checkoutSingleRemovedFile() {
        val fs = Jimfs.newFileSystem(Configuration.unix())!!
        val root = fs.getPath("/projectRoot")
        Files.createDirectories(root)
        init(root)

        val filePath = root.resolve("file.txt")
        val content = "Hello, World".toByteArray()
        Files.newOutputStream(filePath).use {
            it.write(content)
        }
        add(root, filePath)
        commit(root, "test: file '$filePath' added")

        Files.delete(filePath)
        checkout(root, filePath)
        assertArrayEquals(content, Files.readAllBytes(filePath))
    }

    /*@Test
    fun checkoutPartiallyModifiedRootDirectory() {
        val fs = Jimfs.newFileSystem(Configuration.unix())!!
        val root = fs.getPath("/projectRoot")
        Files.createDirectories(root)
        init(root)




        val filePath = root.resolve("file.txt")
        val content = "Hello, World".toByteArray()
        Files.newOutputStream(filePath).use {
            it.write(content)
        }
        add(root, filePath)
        commit(root, "test: file '$filePath' added")

        Files.delete(filePath)
        checkout(root, filePath)
        assertArrayEquals(content, Files.readAllBytes(filePath))
    }*/
}
