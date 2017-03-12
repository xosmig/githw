package com.xosmig.githw.commands

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.junit.Test
import java.nio.file.Files

class CheckoutCommandTest {

    @Test
    fun singleFileTest() {
        val fs = Jimfs.newFileSystem(Configuration.unix())!!
        val root = fs.getPath("/projectRoot")
        Files.createDirectories(root)
        init(root)
        val filePath = root.resolve("file.txt")
        Files.newOutputStream(filePath).use {
            it.write("Hello, World".toByteArray())
        }
        add(root, filePath)
        commit(root, "test: file '$filePath' added")
//        Files.delete(filePath)
//        checkout(root, filePath)
    }
}
