package com.xosmig.githw.testutils

import com.xosmig.githw.utils.Sha256
import java.nio.file.Files
import java.nio.file.Path

fun getSha256(path: Path): Sha256 {
    var result = Sha256.get(path.fileName.toString())
    if (Files.isDirectory(path)) {
        for (next in Files.newDirectoryStream(path).sorted()) {
            result = result.add(next.fileName.toString())
            result = result.add(getSha256(next))
        }
    } else {
        result = result.add(Files.readAllBytes(path))
    }
    return result
}

fun copy(source: Path, target: Path) {
    if (Files.isDirectory(source)) {
        Files.createDirectories(target)
        for (next in Files.newDirectoryStream(source)) {
            copy(next, target.resolve(source.relativize(next)))
        }
    } else {
        Files.copy(source, target)
    }
}
