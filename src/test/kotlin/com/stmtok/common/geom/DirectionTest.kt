package com.stmtok.common.geom

import org.junit.jupiter.api.Test
import kotlin.math.PI

class DirectionTest {
    @Test
    fun case0() {
        val direction = Direction(
            Vector(1, 0, 0),
            Vector(0, 1, 0),
            -PI / 6,
            Vector(0, 0, 1),
            PI / 4,
        )
        println(direction.vector())

        val direction2 = Direction(
            Vector(0, 1, 0),
            -PI / 6,
            Vector(0, 0, 1),
            PI / 4,
        )
        println(direction2.vector())
    }
}