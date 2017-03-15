package com.xosmig.githw.utils

import org.junit.Test
import org.junit.Assert.*

class CacheTest {

    val events = StringBuilder()

    val a = Cache({ events.append('a') })
    val b = Cache({ events.append('b') }, a)
    val c = Cache({ events.append('c') }, b)
    val d = Cache({ events.append('d') })
    val e = Cache({ events.append('e') }, d, c)

    @Test
    fun testDeps() {
        d.reset()
        b.evaluate()
        e.evaluate()
        a.evaluate()
        a.reset()
        d.reset()
        c.evaluate()

        assertEquals("abdceabc", events.toString())
    }
}
