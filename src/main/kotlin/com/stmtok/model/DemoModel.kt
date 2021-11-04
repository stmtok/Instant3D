package com.stmtok.model

import com.stmtok.common.geom.Point
import com.stmtok.common.geom.Rotation
import com.stmtok.common.geom.Vector
import kotlin.math.sqrt

class DemoModel {
    private val angleStep: Double = 5.0

    var cameraPos = Point(5 * sqrt(3.0), 10.0, 15.0)
        private set
    var cameraRot = Rotation(-30, 30, 0)
        private set

    fun moveCameraLeft() {
        cameraRot = cameraRot.copy(y = cameraRot.y + angleStep)
        val d = cameraPos.distance(Point())
        cameraPos = Point(
            x = d * cameraRot.cx * cameraRot.sy,
            y = d * -cameraRot.sx,
            z = d * cameraRot.cx * cameraRot.cy
        )
    }

    fun moveCameraRight() {
        cameraRot = cameraRot.copy(y = cameraRot.y - angleStep)
        val d = cameraPos.distance(Point())
        cameraPos = Point(
            x = d * cameraRot.cx * cameraRot.sy,
            y = d * -cameraRot.sx,
            z = d * cameraRot.cx * cameraRot.cy
        )
    }

    fun moveCameraUp() {
        cameraRot = cameraRot.copy(x = cameraRot.x + angleStep)
        val d = cameraPos.distance(Point())
        cameraPos = Point(
            x = d * cameraRot.cx * cameraRot.sy,
            y = d * -cameraRot.sx,
            z = d * cameraRot.cx * cameraRot.cy
        )
    }

    fun moveCameraDown() {
        cameraRot = cameraRot.copy(x = cameraRot.x - angleStep)
        val d = cameraPos.distance(Point())
        cameraPos = Point(
            x = d * cameraRot.cx * cameraRot.sy,
            y = d * -cameraRot.sx,
            z = d * cameraRot.cx * cameraRot.cy
        )
    }

    fun cameraZoomIn() {
        cameraPos += (Point() - cameraPos).normal()
    }

    fun cameraZoomOut() {
        cameraPos += (cameraPos - Point()).normal()
    }

    fun resetCamera() {
        cameraPos = Point(5 * sqrt(3.0), 10.0, 15.0)
        cameraRot = Rotation(-30, 30, 0)
    }

    var modelPos = Point()
        private set
    var modelRot = Rotation()
        private set
    var modelScale = 1.0
        private set

    fun moveModelLeft() {
        modelPos -= Vector(1, 0, 0).rotation(modelRot) / modelScale
    }

    fun moveModelRight() {
        modelPos += Vector(1, 0, 0).rotation(modelRot) / modelScale
    }

    fun moveModelDown() {
        modelPos -= Vector(0, 1, 0).rotation(modelRot) / modelScale
    }

    fun moveModelUp() {
        modelPos += Vector(0, 1, 0).rotation(modelRot) / modelScale
    }

    fun rotModelLeft() {
        modelRot = modelRot.multi(Rotation(0.0, -angleStep, 0.0))
    }

    fun rotModelRight() {
        modelRot = modelRot.multi(Rotation(0.0, angleStep, 0.0))
    }

    fun rotModelUp() {
        modelRot = modelRot.multi(Rotation(-angleStep, 0.0, 0.0))
    }

    fun rotModelDown() {
        modelRot = modelRot.multi(Rotation(angleStep, 0.0, 0.0))
    }

    fun modelScaleUp() {
        modelScale *= 2.0
    }

    fun modelScaleDown() {
        modelScale /= 2.0
    }

    fun resetModel() {
        modelPos = Point()
        modelRot = Rotation()
        modelScale = 1.0
        cursorPos = Point()
    }

    var put: Boolean = false
        private set

    fun flipPut() {
        put = !put
    }

    var cursorPos = Point()
        private set

    val r = 0.3
    val o = Point()
    val ro = 1.0
    val p0 = Point(5, 0, 0)
    val r0 = 1.0
    val p1 = Point(0, 5, 0)
    val r1 = 1.0

    val normals: MutableList<Vector> = mutableListOf()

    var randomO: Point? = null
        private set

    var random0: Point? = null
        private set

    var random1: Point? = null
        private set


    fun updateRandoms() {
        if (put && normals.size < 10000) {
            val ov = getRandomPoint(o, ro).also {
                randomO = it
            }
            val pv0 = getRandomPoint(p0, r0).also {
                random0 = it
            }.toVector(ov)
            val pv1 = getRandomPoint(p1, r1).also {
                random1 = it
            }.toVector(ov)
            normals.add(pv0.cross(pv1).normal())
        } else {
            randomO = null
            random0 = null
            random1 = null
        }
    }

    private fun getRandomPoint(p: Point, r: Double): Point {
        var result: Point
        while (true) {
            result = Point(p.x + r * random(), p.y + r * random(), p.z + r * random())
            if (result.distance(p) <= r) break
        }
        return result
    }

    private val range = (-10..10)
    private fun random(): Double = range.random() * 1e-1
}