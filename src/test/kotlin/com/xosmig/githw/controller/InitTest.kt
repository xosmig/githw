package com.xosmig.githw.controller

import com.xosmig.githw.GithwTestBase
import org.junit.Test

class InitTest: GithwTestBase() {

    @Test(expected = IllegalArgumentException::class)
    fun initFailRepositoryExistsTest() {
        BasicGithwController.init(root)
    }
}
