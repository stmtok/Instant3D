package com.stmtok.view

import kotlin.math.pow
import kotlin.math.sqrt

data class Vector(
    val x: Double,
    val y: Double,
    val z: Double
) {
    constructor(x: Float, y: Float, z: Float) : this(x.toDouble(), y.toDouble(), z.toDouble())
    constructor(x: Long, y: Long, z: Long) : this(x.toDouble(), y.toDouble(), z.toDouble())
    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())

    operator fun plus(v: Vector): Vector = Vector(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vector): Vector = Vector(x - v.x, y - v.y, z - v.z)
    operator fun unaryMinus(): Vector = Vector(-x, -y, -z)
    operator fun times(k: Double): Vector = Vector(x * k, y * k, z * k)
    operator fun times(k: Float): Vector = times(k.toDouble())
    operator fun times(k: Long): Vector = times(k.toDouble())
    operator fun times(k: Int): Vector = times(k.toDouble())
    operator fun div(k: Double): Vector = Vector(x / k, y / k, z / k)
    operator fun div(k: Float): Vector = div(k.toDouble())
    operator fun div(k: Long): Vector = div(k.toDouble())
    operator fun div(k: Int): Vector = div(k.toDouble())

    val square: Double
        get() = x.pow(2) + y.pow(2) + z.pow(2)
    val length: Double
        get() = sqrt(square)

    fun dot(v: Vector): Double = Companion.dot(this, v)
    fun cross(v: Vector): Vector = Companion.cross(this, v)
    fun normal(): Vector {
        if (length == 0.0) {
            return this
        }
        return this / length
    }

    fun toPoint(base: Point = Point()): Point = base + this

    companion object {
        fun dot(v0: Vector, v1: Vector): Double {
            return v0.x * v1.x + v0.y * v1.y + v0.z * v1.z
        }

        fun cross(v0: Vector, v1: Vector): Vector {
            return Vector(
                v0.y * v1.z - v0.z * v1.y,
                v0.z * v1.x - v0.x * v1.z,
                v0.x * v1.y - v0.y * v1.x,
            )
        }
    }
}