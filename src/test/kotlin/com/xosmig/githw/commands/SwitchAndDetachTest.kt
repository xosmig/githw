package com.xosmig.githw.commands

import com.xosmig.githw.GithwTestClass
import com.xosmig.githw.utils.FilesUtils.countSha256
import org.junit.Test
import com.natpryce.hamkrest.assertion.*
import com.natpryce.hamkrest.equalTo
import java.nio.file.Files.*

class SwitchAndDetachTest : GithwTestClass() {

    @Test
    fun detachTest() {
        val dir = root.resolve("dir")
        createDirectories(dir)

        randomUtils.randomDirectory(dir)
        githw.addAll()
        githw.commit("a")
        val aCommit = githw.commit
        val aHash = countSha256(dir)

        randomUtils.randomDirectory(dir)
        githw.addAll()
        githw.commit("aa")
        val aaCommit = githw.commit
        val aaHash = countSha256(dir)
        assertThat(aaHash, !equalTo(aHash))

        githw.detach(aCommit.sha256)
        assertThat(githw.commit, equalTo(aCommit))
        assertThat(countSha256(dir), equalTo(aHash))

        githw.detach(aaCommit.sha256)
        assertThat(githw.commit, equalTo(aaCommit))
        assertThat(countSha256(dir), equalTo(aaHash))
    }
}
