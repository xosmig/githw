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
        init(root)
        val rootTree1 = GitTree.create(gitDir, emptyMap())

        val helloWorldPath = fs.getPath("foo/bar/baz/helloWorld.txt")
        val helloWorld = "Hello, World!".toByteArray()
        val rootTree2 = rootTree1.putFile(helloWorldPath, helloWorld)

        val hiWorldPath = fs.getPath("foo/hiWorld.txt")
        val hiWorld = "Hi, World!".toByteArray()
        val rootTree3 = rootTree2.putFile(hiWorldPath, hiWorld)

        rootTree3.writeToDisk()

        val copyTree = GitObject.load(gitDir, rootTree3.sha256) as GitTree
        assertArrayEquals((copyTree.resolve(helloWorldPath).loaded as GitFile).content, helloWorld)
        assertArrayEquals((copyTree.resolve(hiWorldPath).loaded as GitFile).content, hiWorld)
    }
}
