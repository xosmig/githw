package com.xosmig.githw.refs

import com.xosmig.githw.TAGS_PATH
import com.xosmig.githw.objects.GitObject
import com.xosmig.githw.objects.GitObjectFromDisk
import com.xosmig.githw.utils.Sha256
import java.io.IOException
import java.io.ObjectInputStream
import java.nio.file.Files
import java.nio.file.Path

class Tag(private val gitDir: Path, val name: String, val commit: GitObject) {
    companion object {
        @Throws(IOException::class)
        fun load(gitDir: Path, branchName: String): Tag {
            Files.newInputStream(gitDir.resolve(TAGS_PATH).resolve(branchName)).use {
                ObjectInputStream(it).use {
                    return Tag(gitDir, branchName, GitObjectFromDisk(gitDir, it.readObject() as Sha256))
                }
            }
        }
    }
}
