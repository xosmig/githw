package com.xosmig.githw.objects

import com.xosmig.githw.utils.Sha256
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Path

class GitFile private constructor( gitDir: Path,
                                   val content: ByteArray,
                                   knownSha256: Sha256? ): GitObjectLoaded(gitDir, knownSha256) {

    companion object {
        fun load(gitDir: Path, sha256: Sha256, ins: ObjectInputStream): GitFile {
            val content = ins.readObject() as ByteArray
            return GitFile(gitDir, content, sha256)
        }

        fun create(gitDir: Path, content: ByteArray):  GitFile {
            return GitFile(gitDir, content, knownSha256 = null)
        }
    }

    override fun writeContentTo(out: ObjectOutputStream) = out.writeObject(content)
}
