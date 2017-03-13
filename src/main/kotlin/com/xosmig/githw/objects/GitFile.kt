package com.xosmig.githw.objects

import com.xosmig.githw.utils.Sha256
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Path

class GitFile private constructor( gitDir: Path,
                                   val content: ByteArray,
                                   onDisk: Boolean ): GitObjectLoaded(gitDir, onDisk) {

    override val sha256: Sha256
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    companion object {
        fun load(gitDir: Path, ins: ObjectInputStream): GitFile {
            val content = ins.readObject() as ByteArray
            return GitFile(gitDir, content, onDisk = true)
        }

        fun create(gitDir: Path, content: ByteArray):  GitFile {
            return GitFile(gitDir, content, onDisk = false)
        }
    }

    override fun writeContentTo(out: ObjectOutputStream) = out.writeObject(content)
}
