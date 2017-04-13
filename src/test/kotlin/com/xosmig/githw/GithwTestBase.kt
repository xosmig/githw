package com.xosmig.githw

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.xosmig.githw.controller.BasicGithwController
import com.xosmig.githw.controller.GithwController
import com.xosmig.githw.testutils.RandomUtils
import java.nio.file.FileSystem
import java.nio.file.Path

abstract class GithwTestBase {
    val fs: FileSystem = Jimfs.newFileSystem(Configuration.unix())
    val rootDirName = "projectRoot"
    val root: Path = fs.getPath("/$rootDirName")
    val randomUtils = RandomUtils()
    val gitDir: Path = root.resolve(GIT_DIR_PATH)
    val githw: GithwController = BasicGithwController.init(root)
}
