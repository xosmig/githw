package com.xosmig.githw.objects

import com.xosmig.githw.GithwTestClass
import com.xosmig.githw.objects.GitTree.Companion.createEmptyTree
import com.xosmig.githw.objects.GitObjectFromDisk.Companion.loadObject
import org.junit.Assert.*
import org.junit.Test

class GitObjectTest: GithwTestClass() {

    @Test
    fun saveAndLoadFilesSimple() {
        val rootTree1 = githw.createEmptyTree()

        val helloWorldPath = fs.getPath("foo/bar/baz/helloWorld.txt")
        val helloWorld = "Hello, World!".toByteArray()
        val rootTree2 = rootTree1.putFile(helloWorldPath, helloWorld)

        val hiWorldPath = fs.getPath("foo/hiWorld.txt")
        val hiWorld = "Hi, World!".toByteArray()
        val rootTree3 = rootTree2.putFile(hiWorldPath, hiWorld)

        rootTree3.writeToDisk()

        val copyTree = githw.loadObject(rootTree3.sha256) as GitTree
        assertArrayEquals((copyTree.resolve(helloWorldPath)!!.loaded as GitFile).content, helloWorld)
        assertArrayEquals((copyTree.resolve(hiWorldPath)!!.loaded as GitFile).content, hiWorld)
    }
}
