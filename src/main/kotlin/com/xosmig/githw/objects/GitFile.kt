package com.xosmig.githw.objects

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Path

class GitFile(gitDir: Path, val content: ByteArray): GitObjectLoaded(gitDir) {
    companion object {
        fun load(gitDir: Path, ins: ObjectInputStream): GitFile = GitFile(gitDir, ins.readObject() as ByteArray)
    }

    override fun writeContentTo(out: ObjectOutputStream) = out.writeObject(content)
}
