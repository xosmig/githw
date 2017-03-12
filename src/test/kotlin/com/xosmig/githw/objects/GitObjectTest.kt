package com.xosmig.githw.objects

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.xosmig.githw.GIT_DIR_PATH
import com.xosmig.githw.commands.init
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files

class GitObjectTest {

    @Test
    fun saveAndLoadFilesSimple() {
        val fs = Jimfs.newFileSystem(Configuration.unix())!!
        val root = fs.getPath("/projectRoot")
        Files.createDirectories(root)
        val gitDir = root.resolve(GIT_DIR_PATH)

        val rootTree = GitTree(gitDir, emptyMap())
        init(root)
        val helloWorld = "Hello, World!".toByteArray()
        rootTree.putFile(fs.getPath("foo/bar/baz/helloWorld.txt"), helloWorld)
        val hiWorld = "Hi, World!".toByteArray()
        rootTree.putFile(fs.getPath("foo/hiWorld.txt"), hiWorld)
        rootTree.writeToDisk()

        val copyTree = GitObjectFromDisk(gitDir, rootTree.getSha256()).loaded as GitTree
        val foo = copyTree.createPath(fs.getPath("foo"))
        val baz = foo.createPath(fs.getPath("bar/baz"))

        assertArrayEquals((foo.getChild("hiWorld.txt").loaded as GitFile).content, hiWorld)
        assertArrayEquals((baz.getChild("helloWorld.txt").loaded as GitFile).content, helloWorld)
    }
}
