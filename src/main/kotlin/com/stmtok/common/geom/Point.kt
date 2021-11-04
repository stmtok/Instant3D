package com.stmtok.common.geom

import kotlin.math.pow
import kotlin.math.sqrt

data class Point(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0
) {
    constructor(x: Float, y: Float, z: Float) : this(x.toDouble(), y.toDouble(), z.toDouble())
    constructor(x: Long, y: Long, z: Long) : this(x.toDouble(), y.toDouble(), z.toDouble())
    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())

    operator fun plus(v: Vector): Point = Point(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vector): Point = Point(x - v.x, y - v.y, z - v.z)
    operator fun minus(p: Point): Vector = Vector(x - p.x, y - p.y, z - p.z)

    fun square(p: Point): Double = Companion.square(this, p)
    fun distance(p: Point): Double = Companion.distance(this, p)

    fun toVector(base: Point = Point()): Vector = this - base
    fun transform(mat: Matrix): Vector {
        val result = mat * toVector().matrix
        return Vector(result[0, 3], result[1, 3], result[2, 3])
    }
    companion object {
        fun square(p0: Point, p1: Point): Double {
            return (p1.x - p0.x).pow(2) + (p1.y - p0.y).pow(2) + (p1.z - p0.z).pow(2)
        }

        fun distance(p0: Point, p1: Point): Double {
            return sqrt(square(p0, p1))
        }
    }
}