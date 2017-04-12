package com.xosmig.githw.controller

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.xosmig.githw.GithwTestBase
import com.xosmig.githw.utils.FilesUtils
import com.xosmig.githw.utils.FilesUtils.countSha256
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files.exists

class SwitchTest : GithwTestBase() {

    @Test
    fun integrationSimple() {
        val dir = root.resolve("dir")
        githw.newBranch("other")

        // in 'master':
        randomUtils.randomDirectory(dir)
        githw.addAll()
        githw.commit("a")
        val aHash = FilesUtils.countSha256(dir)

        githw.switchBranch("other")
        assertFalse(exists(dir))

        // in 'other':
        randomUtils.randomDirectory(dir)
        githw.addAll()
        githw.commit("b")
        val bHash = FilesUtils.countSha256(dir)
        assertThat(aHash, !equalTo(bHash))

        githw.switchBranch("master")
        assertThat(countSha256(dir), equalTo(aHash))

        githw.switchBranch("other")
        assertThat(countSha256(dir), equalTo(bHash))
    }
}

