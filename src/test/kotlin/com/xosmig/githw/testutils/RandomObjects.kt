package com.xosmig.githw.testutils

import java.lang.Math.min
import java.nio.file.Files.*
import java.nio.file.Path
import java.util.*

class RandomObjects(seed: Long): Random(seed) {

    companion object {
        val ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_ "
    }

    constructor(): this(827)

    fun nextIntClosed(bound: Int) = nextInt(bound + 1)

    fun nextString(length: Int = nextInt(20) + 1): String {
        val builder = StringBuilder()
        for (i in 1..length) {
            builder.append(ALPHABET[nextInt(ALPHABET.length)])
        }
        return builder.toString()
    }

    fun randomDirectory(root: Path,
                        maxNumberOfDirectories: Int = nextInt(100) + 1,
                        maxNumberOfFiles: Int = nextInt(100) + 1,
                        maxSubdirNumber: Int = nextInt(10) + 1,
                        maxNumOfFilesInOneDir: Int = nextInt(10) + 1,
                        maxFileSize: Int = nextInt(1000) + 1,
                        emptyFileProbability: Double = 0.05,
                        allowEmptyDirectories: Boolean = false) {

        fun impl(root: Path, maxDirsNum: Int, maxFilesNum: Int) {
            val dirs = nextIntClosed(min(maxDirsNum, maxSubdirNumber))
            val files = run {
                val res = nextIntClosed(min(maxFilesNum, maxNumOfFilesInOneDir))
                if (dirs == 0 && res == 0 && !allowEmptyDirectories) { 1 } else { res }
            }

            var dirsLeft = maxDirsNum - dirs
            var filesLeft = maxFilesNum - files

            for (i in 1..dirs) {
                val nextPath = root.resolve(nextString())
                createDirectories(nextPath)
                val spendDirs = nextIntClosed(dirsLeft)
                val spendFiles = nextIntClosed(filesLeft)
                dirsLeft -= spendDirs
                filesLeft -= spendFiles
                impl(nextPath, spendDirs, spendFiles)
            }

            for (i in 1..files) {
                val nextPath = root.resolve(nextString())
                if (nextDouble() <= emptyFileProbability) {
                    // empty file
                    createFile(nextPath)
                    continue
                }
                newOutputStream(nextPath).use {
                    val content = ByteArray(nextIntClosed(maxFileSize) + 1)
                    nextBytes(content)
                    it.write(content)
                }
            }
        }

        impl(root, maxNumberOfDirectories, maxNumberOfFiles)
    }
}
