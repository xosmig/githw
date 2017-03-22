package com.xosmig.githw.objects

import com.xosmig.githw.controller.GithwController
import com.xosmig.githw.utils.Sha256
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files.*
import java.nio.file.Path

class GitFile private constructor( githw: GithwController,
                                   val content: ByteArray,
                                   knownSha256: Sha256? ): GitFSObject(githw, knownSha256) {

    companion object {
        internal fun GithwController.loadFile(sha256: Sha256, ins: ObjectInputStream): GitFile {
            val content = ins.readObject() as ByteArray
            return GitFile(this, content, sha256)
        }

        fun GithwController.createFile(content: ByteArray):  GitFile {
            return GitFile(this, content, knownSha256 = null)
        }
    }

    override fun writeContentTo(out: ObjectOutputStream) = out.writeObject(content)

    override fun revert(path: Path) {
        if (path.parent != null) {
            createDirectories(path.parent)
        }
        newOutputStream(path).use {
            it.write(content)
        }
    }

    override fun toString(): String = "@file"

    override fun mergeWith(other: GitFSObject, path: Path, newFiles: MutableList<Path>) {
        if (other.sha256 == sha256) {
            return
        }
        other.revert(resolveConflictPath(path, newFiles))
    }
}
