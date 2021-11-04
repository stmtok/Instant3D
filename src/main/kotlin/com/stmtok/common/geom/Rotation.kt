package com.stmtok.common.geom

import kotlin.math.*

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
    private val rx: Double
        get() = PI * x / 180

    /** y軸周りの回転角 (radian) */
    private val ry: Double
        get() = PI * y / 180

    /** z軸周りの回転角 (radian) */
    private val rz: Double
        get() = PI * z / 180

    /** sin(x) */
    val sx: Double
        get() = sin(rx)

    /** sin(y) */
    val sy: Double
        get() = sin(ry)

    /** sin(z) */
    val sz: Double
        get() = sin(rz)

    /** cos(x) */
    val cx: Double
        get() = cos(rx)

    /** cos(y) */
    val cy: Double
        get() = cos(ry)

    /** cos(z) */
    val cz: Double
        get() = cos(rz)

    private val xMatrix: Matrix by lazy {
        Matrix(
            listOf(
                listOf(1.0, 0.0, 0.0, 0.0),
                listOf(0.0, cx, -sx, 0.0),
                listOf(0.0, sx, cx, 0.0),
                listOf(0.0, 0.0, 0.0, 1.0)
            )
        )
    }

    private val yMatrix: Matrix by lazy {
        Matrix(
            listOf(
                listOf(cy, 0.0, sy, 0.0),
                listOf(0.0, 1.0, 0.0, 0.0),
                listOf(-sy, 0.0, cy, 0.0),
                listOf(0.0, 0.0, 0.0, 1.0)
            )
        )
    }

    private val zMatrix: Matrix by lazy {
        Matrix(
            listOf(
                listOf(cz, -sz, 0.0, 0.0),
                listOf(sz, cz, 0.0, 0.0),
                listOf(0.0, 0.0, 1.0, 0.0),
                listOf(0.0, 0.0, 0.0, 1.0)
            )
        )
    }

    val matrix: Matrix by lazy { (xMatrix * yMatrix * zMatrix) }

    fun multi(rot: Rotation): Rotation {
        return get(rot.matrix * matrix)
    }

    companion object {
        fun get(mat: Matrix): Rotation {
            val sx = mat[0, 0] * mat[0, 0] + mat[0, 1] * mat[0, 1] + mat[0, 2] * mat[0, 2]
            val sy = mat[1, 0] * mat[1, 0] + mat[1, 1] * mat[1, 1] + mat[1, 2] * mat[1, 2]
            val sz = mat[2, 0] * mat[2, 0] + mat[2, 1] * mat[2, 1] + mat[2, 2] * mat[2, 2]
            val rot = Matrix(
                listOf(
                    listOf(mat[0, 0] * sx, mat[0, 1] * sx, mat[0, 2] * sx, 0.0),
                    listOf(mat[1, 0] * sy, mat[1, 1] * sy, mat[1, 2] * sy, 0.0),
                    listOf(mat[2, 0] * sz, mat[2, 1] * sz, mat[2, 2] * sz, 0.0),
                    listOf(0.0, 0.0, 0.0, 1.0)
                )
            )
            var x = atan2(-rot[1, 2], rot[2, 2])
            var y = asin(rot[0, 2])
            var z = atan2(-rot[0, 1], rot[0, 0])
            if (rot[0, 2] == 1.0) {
                x = atan2(rot[2, 1], rot[1, 1])
                y = PI / 2
                z = 0.0
            } else if (rot[0, 2] == -1.0) {
                x = atan2(rot[2, 1], rot[1, 1])
                y = -PI / 2
                z = 0.0
            }
            return Rotation(180 * x / PI, 180 * y / PI, 180 * z / PI)
        }
    }
}