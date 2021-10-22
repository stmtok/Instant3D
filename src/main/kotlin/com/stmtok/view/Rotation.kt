package com.stmtok.view

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class Rotation(
    /** x軸周りの回転角 (degree) */
    val x: Double = 0.0,
    /** y軸周りの回転角 (degree)  */
    val y: Double = 0.0,
    /** z軸周りの回転角 (degree)  */
    val z: Double = 0.0
) {
    constructor(x: Float, y: Float, z: Float) : this(x.toDouble(), y.toDouble(), z.toDouble())
    constructor(x: Long, y: Long, z: Long) : this(x.toDouble(), y.toDouble(), z.toDouble())
    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())

    /** x軸周りの回転角 (radian) */
    val rx: Double
        get() = PI * x / 180

    /** y軸周りの回転角 (radian) */
    val ry: Double
        get() = PI * y / 180

    /** z軸周りの回転角 (radian) */
    val rz: Double
        get() = PI * z / 180

    val sx: Double
        get() = sin(rx)
    val sy: Double
        get() = sin(ry)
    val sz: Double
        get() = sin(rz)
    val cx: Double
        get() = cos(rx)
    val cy: Double
        get() = cos(ry)
    val cz: Double
        get() = cos(rz)
}