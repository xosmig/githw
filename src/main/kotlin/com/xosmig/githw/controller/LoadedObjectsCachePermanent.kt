package com.xosmig.githw.controller

import com.xosmig.githw.objects.GitObjectLoaded
import com.xosmig.githw.utils.Sha256
import java.util.*

class LoadedObjectsCachePermanent: LoadedObjectsBank {
    private val underlying = HashMap<Sha256, GitObjectLoaded>()

    override fun get(sha256: Sha256): GitObjectLoaded? = underlying[sha256]

    override fun getOrPut(sha256: Sha256, supplier: () -> GitObjectLoaded): GitObjectLoaded {
        return underlying.getOrPut(sha256, supplier)
    }
}
