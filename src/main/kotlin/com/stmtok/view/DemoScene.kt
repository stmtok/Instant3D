package com.stmtok.view

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2
import com.jogamp.opengl.util.FPSAnimator
import com.stmtok.common.gl.*
import com.stmtok.common.gl.Colors.blue
import com.stmtok.common.gl.Colors.cyan
import com.stmtok.common.gl.Colors.darkGreen
import com.stmtok.common.gl.Colors.gray
import com.stmtok.common.gl.Colors.green
import com.stmtok.common.gl.Colors.lightGray
import com.stmtok.common.gl.Colors.orange
import com.stmtok.common.gl.Colors.red
import com.stmtok.common.gl.Colors.yellow
import com.stmtok.common.gl.Helper.drawLine
import com.stmtok.common.gl.Helper.drawSphere
import com.stmtok.common.gl.Helper.glut
import com.stmtok.common.gl.Helper.solidSphere
import com.stmtok.common.geom.Point
import com.stmtok.common.geom.Rotation
import com.stmtok.common.geom.Vector
import com.stmtok.model.DemoModel
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.nio.FloatBuffer
import javax.swing.JFrame
import kotlin.math.sqrt

class DemoScene(private val model: DemoModel) : DimensionDrawer() {

    override fun getCameraPosition(): Point = model.cameraPos
    override fun getCameraRotation(): Rotation = model.cameraRot

    override fun getModelPosition(): Point = model.modelPos
    override fun getModelRotation(): Rotation = model.modelRot
    override fun getModelScale(): Double = model.modelScale

    override fun displaySync() {
        model.updateRandoms()
    }

    override fun drawVRSpace(gl: GL) {
        // 背景色を設定
        gl.glClearColor(0.01f, 0.01f, 0.08f, 1.0f)
        gl.glClear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT)

        camera(gl.gL2)
        drawAxis(gl.gL2)
        solidSphere(gl.gL2, model.cursorPos, 0.2, yellow)
    }

    override fun drawModelSpace(gl: GL) = with(gl.gL2) {
        gl.gL2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, lightGray, 0)
        glut.glutWireCube(20f)

        model.randomO?.also { solidSphere(this, it, model.r, gray) }
        model.random0?.also { solidSphere(this, it, model.r, orange) }
        model.random1?.also { solidSphere(this, it, model.r, darkGreen) }

        drawSphere(this, model.o, model.ro, gray)
        drawSphere(this, model.p0, model.r0, orange)
        drawSphere(this, model.p1, model.r1, darkGreen)

        val buf = FloatBuffer.allocate(1)
        glGetFloatv(GL.GL_POINT_SIZE, buf)
        glPointSize(5f)
        gl.gL2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, cyan, 0)
        glBegin(GL.GL_POINTS)
        model.normals.forEach {
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
            gl.glTranslated(model.cameraPos.x, model.cameraPos.y, model.cameraPos.z)
            gl.glRotated(model.cameraRot.x, 1.0, 0.0, 0.0)
            gl.glRotated(model.cameraRot.y, 0.0, 1.0, 0.0)
            gl.glRotated(model.cameraRot.z, 0.0, 0.0, 1.0)
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

}
