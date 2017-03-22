package com.xosmig.githw

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.xosmig.githw.controller.BasicGithwController
import com.xosmig.githw.testutils.RandomUtils
import org.junit.Before
import java.nio.file.Files.createDirectories

abstract class GithwTestClass {
    val fs = Jimfs.newFileSystem(Configuration.unix())!!
    val rootDirName = "projectRoot"
    val root = fs.getPath("/$rootDirName")!!
    val randomUtils = RandomUtils()
    val gitDir = root.resolve(GIT_DIR_PATH)!!
    val githw = BasicGithwController.init(root)
}
