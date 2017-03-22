package com.xosmig.githw.utils

import org.junit.Test
import org.junit.Assert.*

class CacheTest {

    val events = StringBuilder()

    val a = cache { events.append('a') }
    val b = cache(a) { events.append('b') }
    val c = cache(b) { events.append('c') }
    val d = cache { events.append('d') }
    val e = cache(d, c) { events.append('e') }

    @Test
    fun testDeps() {
        d.reset()
        b.update()
        e.update()
        a.update()
        a.reset()
        d.reset()
        c.update()

        assertEquals("abdceabc", events.toString())
    }
}
