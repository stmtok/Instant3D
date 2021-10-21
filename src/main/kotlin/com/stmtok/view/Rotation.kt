package com.stmtok.view

data class Rotation(
    /** x軸周りの回転角 */
    val x: Double = 0.0,
    /** x軸周りの回転角 */
    val y: Double = 0.0,
    /** x軸周りの回転角 */
    val z: Double = 0.0
) {
    constructor(x: Float, y: Float, z: Float) : this(x.toDouble(), y.toDouble(), z.toDouble())
    constructor(x: Long, y: Long, z: Long) : this(x.toDouble(), y.toDouble(), z.toDouble())
    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())
}