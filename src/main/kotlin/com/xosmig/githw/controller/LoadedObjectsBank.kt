package com.xosmig.githw.controller

import com.xosmig.githw.objects.GitObjectLoaded
import com.xosmig.githw.utils.Sha256

interface LoadedObjectsBank {
    fun get(sha256: Sha256): GitObjectLoaded?
    fun getOrPut(sha256: Sha256, supplier: () -> GitObjectLoaded): GitObjectLoaded
}
