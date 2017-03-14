package com.xosmig.githw.objects

import java.nio.file.Path
import com.xosmig.githw.utils.Sha256

abstract class GitFSObject internal constructor(gitDir: Path,
                                                knownSha256: Sha256? ): GitObjectLoaded(gitDir, knownSha256) {

    abstract fun revert(path: Path)
}
