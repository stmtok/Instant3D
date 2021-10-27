package com.stmtok.gl

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2
import com.jogamp.opengl.util.gl2.GLUT
import com.stmtok.geom.Point
import java.nio.FloatBuffer

object Helper {
    val glut = GLUT()

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

    fun drawSphere(gl: GL2, center: Point, radius: Double, color: FloatArray) {
        gl.glPushMatrix()
        gl.glTranslated(center.x, center.y, center.z)
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, color, 0)
        glut.glutWireSphere(radius, 20, 10)
        gl.glPopMatrix()
    }

    fun solidSphere(gl: GL2, center: Point, radius: Double, color: FloatArray) {
        gl.glPushMatrix()
        gl.glTranslated(center.x, center.y, center.z)
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, color, 0)
        glut.glutSolidSphere(radius, 20, 10)
        gl.glPopMatrix()
    }
}