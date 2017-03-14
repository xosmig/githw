package com.xosmig.githw.testutils

import com.xosmig.githw.utils.Sha256
import java.nio.file.Files.*
import java.nio.file.Path

fun getSha256(path: Path): Sha256 {
    var result = Sha256.get(path.fileName.toString())
    if (isDirectory(path)) {
        for (next in newDirectoryStream(path).sorted()) {
            result = result.add(next.fileName.toString())
            result = result.add(getSha256(next))
        }
    } else {
        result = result.add(readAllBytes(path))
    }
    return result
}
