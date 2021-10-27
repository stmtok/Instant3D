package com.stmtok

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2
import com.jogamp.opengl.util.FPSAnimator
import com.stmtok.gl.*
import com.stmtok.gl.Colors.blue
import com.stmtok.gl.Colors.cyan
import com.stmtok.gl.Colors.darkGreen
import com.stmtok.gl.Colors.gray
import com.stmtok.gl.Colors.green
import com.stmtok.gl.Colors.lightGray
import com.stmtok.gl.Colors.orange
import com.stmtok.gl.Colors.red
import com.stmtok.gl.Colors.yellow
import com.stmtok.gl.Helper.drawLine
import com.stmtok.gl.Helper.drawSphere
import com.stmtok.gl.Helper.glut
import com.stmtok.gl.Helper.solidSphere
import com.stmtok.geom.Point
import com.stmtok.geom.Rotation
import com.stmtok.geom.Vector
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.nio.FloatBuffer
import javax.swing.JFrame
import kotlin.math.sqrt

class DemoScene : DimensionDrawer(), KeyListener {
    private val angleStep: Double = 5.0

    private var cameraPos = Point(5 * sqrt(3.0), 10.0, 15.0)
    private var cameraRot = Rotation(-30, 30, 0)
    override fun getCameraPosition(): Point = cameraPos
    override fun getCameraRotation(): Rotation = cameraRot

    private var modelPos = Point()
    private var modelRot = Rotation()
    private var modelScale = 1.0
    override fun getModelPosition(): Point = modelPos
    override fun getModelRotation(): Rotation = modelRot
    override fun getModelScale(): Double = modelScale
    private var cursorPos = Point()

    private val r = 0.3
    private val o = Point()
    private val ro = 1.0
    private val p0 = Point(5, 0, 0)
    private val r0 = 1.0
    private val p1 = Point(0, 5, 0)
    private val r1 = 1.0

    private val normals: MutableList<Vector> = mutableListOf()

    private var randomO: Point? = null
    private var random0: Point? = null
    private var random1: Point? = null
    private var put: Boolean = false

    override fun displaySync() {
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

    override fun drawVRSpace(gl: GL) {
        // 背景色を設定
        gl.glClearColor(0.01f, 0.01f, 0.08f, 1.0f)
        gl.glClear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT)

        camera(gl.gL2)
        drawAxis(gl.gL2)
        solidSphere(gl.gL2, cursorPos, 0.2, yellow)
    }

    override fun drawModelSpace(gl: GL) = with(gl.gL2) {
        gl.gL2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, lightGray, 0)
        glut.glutWireCube(20f)

        randomO?.also { solidSphere(this, it, r, gray) }
        random0?.also { solidSphere(this, it, r, orange) }
        random1?.also { solidSphere(this, it, r, darkGreen) }

        drawSphere(this, o, ro, gray)
        drawSphere(this, p0, r0, orange)
        drawSphere(this, p1, r1, darkGreen)

        val buf = FloatBuffer.allocate(1)
        glGetFloatv(GL.GL_POINT_SIZE, buf)
        glPointSize(5f)
        gl.gL2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, cyan, 0)
        glBegin(GL.GL_POINTS)
        normals.forEach {
            glVertex3d(it.x, it.y, it.z)
        }
        glEnd()
    }

    private fun drawAxis(gl: GL2) {
        val origin = Point(0.0, 0.0, 0.0)
        // x軸の表示
        drawLine(gl, origin, Point(distance * 0.5, 0.0, 0.0), width = 4f, red)
        // y軸の表示
        drawLine(gl, origin, Point(0.0, distance * 0.5, 0.0), width = 4f, green)
        // z軸の表示
        drawLine(gl, origin, Point(0.0, 0.0, distance * 0.5), width = 4f, blue)
    }

    private fun camera(gl: GL2) {
        if (dimension != Dimension.FREE) {
            gl.glPushMatrix()
            gl.glPolygonMode(GL.GL_FRONT, GL2.GL_LINE)
            gl.glTranslated(cameraPos.x, cameraPos.y, cameraPos.z)
            gl.glRotated(cameraRot.x, 1.0, 0.0, 0.0)
            gl.glRotated(cameraRot.y, 0.0, 1.0, 0.0)
            gl.glRotated(cameraRot.z, 0.0, 0.0, 1.0)
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, floatArrayOf(0f, 1f, .5f, .3f), 0)
            gl.glBegin(GL.GL_TRIANGLE_FAN)
            gl.glVertex3f(0f, 0f, 0f)
            gl.glVertex3f(-0.5f, 0.3f, -1f)
            gl.glVertex3f(0.5f, 0.3f, -1f)
            gl.glVertex3f(0.5f, -0.3f, -1f)
            gl.glVertex3f(-0.5f, -0.3f, -1f)
            gl.glVertex3f(-0.5f, 0.3f, -1f)
            gl.glEnd()
            gl.glPolygonMode(GL.GL_FRONT, GL2.GL_FILL)
            gl.glPopMatrix()
        }
    }

    override fun keyTyped(e: KeyEvent) = Unit
    override fun keyReleased(e: KeyEvent) {
    }

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_LEFT -> {
                if (e.isShiftDown) {
                    // x方向平行移動
                    modelPos -= Vector(1, 0, 0).rotation(modelRot) / modelScale
                } else {
                    modelRot = modelRot.multi(Rotation(0.0, -angleStep, 0.0))
                }
            }
            KeyEvent.VK_RIGHT -> {
                if (e.isShiftDown) {
                    // -x方向平行移動
                    modelPos += Vector(1, 0, 0).rotation(modelRot) / modelScale
                } else {
                    modelRot = modelRot.multi(Rotation(0.0, angleStep, 0.0))
                }
            }
            KeyEvent.VK_UP -> {
                if (e.isShiftDown) {
                    // y方向平行移動
                    modelPos += Vector(0, 1, 0).rotation(modelRot) / modelScale
                } else {
                    modelRot = modelRot.multi(Rotation(-angleStep, 0.0, 0.0))
                }
            }
            KeyEvent.VK_DOWN -> {
                if (e.isShiftDown) {
                    // -y方向平行移動
                    modelPos -= Vector(0, 1, 0).rotation(modelRot) / modelScale
                } else {
                    modelRot = modelRot.multi(Rotation(angleStep, 0.0, 0.0))
                }
            }
            KeyEvent.VK_D -> {
                cameraRot = cameraRot.copy(y = cameraRot.y - angleStep)
                val d = cameraPos.distance(Point())
                cameraPos = Point(
                    x = d * cameraRot.cx * cameraRot.sy,
                    y = d * -cameraRot.sx,
                    z = d * cameraRot.cx * cameraRot.cy
                )
            }
            KeyEvent.VK_A -> {
                cameraRot = cameraRot.copy(y = cameraRot.y + angleStep)
                val d = cameraPos.distance(Point())
                cameraPos = Point(
                    x = d * cameraRot.cx * cameraRot.sy,
                    y = d * -cameraRot.sx,
                    z = d * cameraRot.cx * cameraRot.cy
                )
            }
            KeyEvent.VK_S -> {
                cameraRot = cameraRot.copy(x = cameraRot.x - angleStep)
                val d = cameraPos.distance(Point())
                cameraPos = Point(
                    x = d * cameraRot.cx * cameraRot.sy,
                    y = d * -cameraRot.sx,
                    z = d * cameraRot.cx * cameraRot.cy
                )
            }
            KeyEvent.VK_W -> {
                cameraRot = cameraRot.copy(x = cameraRot.x + angleStep)
                val d = cameraPos.distance(Point())
                cameraPos = Point(
                    x = d * cameraRot.cx * cameraRot.sy,
                    y = d * -cameraRot.sx,
                    z = d * cameraRot.cx * cameraRot.cy
                )
            }
            KeyEvent.VK_Q -> {
                if (e.isShiftDown) {
                    modelScale *= 2.0
                } else {
                    cameraPos += (Point() - cameraPos).normal()
                }
            }
            KeyEvent.VK_Z -> {
                if (e.isShiftDown) {
                    modelScale /= 2.0
                } else {
                    cameraPos += (cameraPos - Point()).normal()
                }
            }
            KeyEvent.VK_C -> {
                cameraPos = Point(5 * sqrt(3.0), 10.0, 15.0)
                cameraRot = Rotation(-30, 30, 0)
            }
            KeyEvent.VK_SPACE -> {
                modelPos = Point()
                modelRot = Rotation()
                modelScale = 1.0
                cursorPos = Point()
            }
            KeyEvent.VK_ENTER -> {
                put = !put
            }
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

fun main() {
    val renderer = DimensionRenderer(800, 800)
//    val renderer = NormalRenderer(800, 600)
//    val renderer = StereoRenderer(1200, 600, 32.0)
    val scene = DemoScene()
    renderer.setSceneDrawer(scene)
    JFrame("Demo").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        contentPane.add(renderer)
        addKeyListener(scene)
        requestFocusInWindow()
        pack()
        isVisible = true
    }
    val animator = FPSAnimator(renderer, 30)
    animator.start()
}
