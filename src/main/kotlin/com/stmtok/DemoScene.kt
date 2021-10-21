package com.stmtok

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2
import com.jogamp.opengl.util.FPSAnimator
import com.jogamp.opengl.util.gl2.GLUT
import com.stmtok.gl.*
import com.stmtok.gl.Colors.blue
import com.stmtok.gl.Colors.cyan
import com.stmtok.gl.Colors.green
import com.stmtok.gl.Colors.magenta
import com.stmtok.gl.Colors.red
import com.stmtok.gl.Colors.white
import com.stmtok.gl.Colors.yellow
import com.stmtok.view.Point
import com.stmtok.view.Rotation
import com.stmtok.view.Vector
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.nio.FloatBuffer
import javax.swing.JFrame
import kotlin.math.sqrt

class DemoScene : DimensionDrawer(), KeyListener {
    private val cursorStep: Double = 0.5
    private val angleStep: Double = 15.0

    override fun getCameraPosition(): Point = Point(5 * sqrt(3.0), 10.0, 15.0)
    override fun getCameraRotation(): Rotation = Rotation(-30, 30, 0)

    private var modelPos = Point()
    private var modelRot = Rotation()
    override fun getModelPosition(): Point = modelPos
    override fun getModelRotation(): Rotation = modelRot
    private var cursorPos = Point()
    private val glut: GLUT = GLUT()

    private val r = 0.3

    private val o = Point()
    private val ro = 0.0
    private val p0 = Point(3, 1, 0)
    private val r0 = 2.0
    private val p1 = Point(1, 3, 0)
    private val r1 = 2.0

    private val normals: MutableList<Vector> = mutableListOf()

    private var randomO: Point? = null
    private var random0: Point? = null
    private var random1: Point? = null
    private var put: Boolean = false

    override fun preDisplay(gl: GL) {
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

        drawAxis(gl.gL2)
        solidSphere(gl.gL2, cursorPos, 0.2, yellow)
    }

    override fun drawModelSpace(gl: GL) = with(gl.gL2) {
//        glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, white, 0)
//        glut.glutSolidTeapot(3.0)
        randomO?.also { solidSphere(this, it, r, white) }
        random0?.also { solidSphere(this, it, r, red) }
        random1?.also { solidSphere(this, it, r, green) }

        drawSphere(this, o, ro, white)
        drawSphere(this, p0, r0, red)
        drawSphere(this, p1, r1, green)

        normals.forEach {
            solidSphere(this, it.toPoint(), 0.05, cyan)
        }

    }

    fun drawLine(gl: GL2, from: Point, to: Point, width: Float = 1f, color: FloatArray) {
        val buf = FloatBuffer.allocate(1)
        gl.glGetFloatv(GL.GL_LINE_WIDTH, buf)
        // 線の太さを設定
        gl.glLineWidth(width)
        // 色を設定
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, color, 0)
        // 線の描画
        gl.glBegin(GL.GL_LINES)
        gl.glVertex3d(from.x, from.y, from.z)
        gl.glVertex3d(to.x, to.y, to.z)
        gl.glEnd()
        gl.glLineWidth(buf.get())
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

    private fun drawSphere(gl: GL2, center: Point, radius: Double, color: FloatArray) {
        gl.glPushMatrix()
        gl.glTranslated(center.x, center.y, center.z)
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, color, 0)
        glut.glutWireSphere(radius, 10, 10)
        gl.glPopMatrix()
    }

    private fun solidSphere(gl: GL2, center: Point, radius: Double, color: FloatArray) {
        gl.glPushMatrix()
        gl.glTranslated(center.x, center.y, center.z)
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, color, 0)
        glut.glutSolidSphere(radius, 10, 10)
        gl.glPopMatrix()
    }

    override fun keyTyped(e: KeyEvent) = Unit
    override fun keyReleased(e: KeyEvent) {
    }

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_LEFT -> {
                modelRot = modelRot.copy(y = modelRot.y + angleStep)
            }
            KeyEvent.VK_RIGHT -> {
                modelRot = modelRot.copy(y = modelRot.y - angleStep)
            }
            KeyEvent.VK_UP -> {
                modelRot = modelRot.copy(x = modelRot.x + angleStep)
            }
            KeyEvent.VK_DOWN -> {
                modelRot = modelRot.copy(x = modelRot.x - angleStep)
            }
            KeyEvent.VK_A -> {
                cursorPos = cursorPos.copy(x = cursorPos.x - cursorStep)
            }
            KeyEvent.VK_D -> {
                cursorPos = cursorPos.copy(x = cursorPos.x + cursorStep)
            }
            KeyEvent.VK_W -> {
                cursorPos = cursorPos.copy(y = cursorPos.y + cursorStep)
            }
            KeyEvent.VK_S -> {
                cursorPos = cursorPos.copy(y = cursorPos.y - cursorStep)
            }
            KeyEvent.VK_Q -> {
                cursorPos = cursorPos.copy(z = cursorPos.z - cursorStep)
            }
            KeyEvent.VK_Z -> {
                cursorPos = cursorPos.copy(z = cursorPos.z + cursorStep)
            }
            KeyEvent.VK_SPACE -> {
                modelPos = Point()
                modelRot = Rotation()
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
    val animator = FPSAnimator(renderer, 60)
    animator.start()
}
