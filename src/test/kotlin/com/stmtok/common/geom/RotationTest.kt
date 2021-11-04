package com.stmtok.common.geom

import org.junit.jupiter.api.Test

class RotationTest {

    @Test
    fun matrix() {
        val x = Rotation(90, 0, 0)
        val mx = Rotation.get(x.matrix)
        println("$x == $mx")

        val y = Rotation(0, 30, 0)
        val my = Rotation.get(y.matrix)
        println("$y == $my")

        val m = Rotation(12, 23, 34)
        val mm = Rotation.get(m.matrix)
        println("$m == $mm")
    }
}