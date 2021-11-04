package com.stmtok.common.gl

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2
import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.glu.GLU
import com.stmtok.common.gl.shader.DistortionShader
import java.awt.Dimension
import kotlin.math.atan2


class StereoRenderer(
    width: Int, height: Int,
    fovy: Double = 110.0, barrel: Float = 0.0f,
) : GLRenderer(fovy, 0.1, 1200.0) {
    private var drawer: SceneDrawer? = null
    private val glu = GLU()

    /** 右目用フレームバッファオブジェクト */
    private val rightFBO: FrameBufferObject = FrameBufferObject()

    /** 左目用フレームバッファオブジェクト */
    private val leftFBO: FrameBufferObject = FrameBufferObject()

    /** 歪みシェーダ */
    private val distortionShader: DistortionShader = DistortionShader()

    /** 目の間隔(cm) */
    private val eyeSpan: Double = 6.0

    /** 目から目標物までの焦点距離(cm) */
    private val targetLength: Double = 100.0

    /** 輻輳角（度）*/
    private val vergence: Double = Math.toDegrees(atan2(eyeSpan * 0.5, targetLength))

    init {
        preferredSize = Dimension(width, height)
        isFocusable = false
        addGLEventListener(this)
        distortionShader.barrel = barrel
    }

    override fun init(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL2
        leftFBO.setup(gl, drawable.surfaceWidth / 2, drawable.surfaceHeight)
        rightFBO.setup(gl, drawable.surfaceWidth / 2, drawable.surfaceHeight)
        distortionShader.setup(gl)
    }

    override fun display(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL2
        val sceneDrawer = getSceneDrawer()
        sceneDrawer?.displaySync()

        gl.glEnable(GL2.GL_LIGHTING)
        gl.glEnable(GL2.GL_LIGHT0)
        gl.glEnable(GL2.GL_LIGHT1)
        gl.glEnable(GL.GL_DEPTH_TEST)

        // 左の映像を生成
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, leftFBO.frameBufferName)
        gl.glMatrixMode(GL2.GL_PROJECTION)
        gl.glLoadIdentity()
        glu.gluPerspective(fovy, leftFBO.width / leftFBO.height.toDouble(), near, far)
        gl.glMatrixMode(GL2.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glRotated(-vergence, 0.0, 1.0, 0.0)
        gl.glTranslated(eyeSpan * 0.5, 0.0, 0.0)
        gl.glViewport(0, 0, leftFBO.width, leftFBO.height)

        sceneDrawer?.drawScene(gl) ?: kotlin.run {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT)
        }

        // 右の映像を生成
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, rightFBO.frameBufferName)
        gl.glMatrixMode(GL2.GL_PROJECTION)
        gl.glLoadIdentity()
        glu.gluPerspective(fovy, rightFBO.width / rightFBO.height.toDouble(), near, far)
        gl.glMatrixMode(GL2.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glRotated(vergence, 0.0, 1.0, 0.0)
        gl.glTranslated(-eyeSpan * 0.5, 0.0, 0.0)
        gl.glViewport(0, 0, rightFBO.width, rightFBO.height)

        sceneDrawer?.drawScene(gl) ?: kotlin.run {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT)
        }

        gl.glDisable(GL.GL_DEPTH_TEST)
        gl.glDisable(GL2.GL_LIGHT1)
        gl.glDisable(GL2.GL_LIGHT0)
        gl.glDisable(GL2.GL_LIGHTING)

        // 画面に表示
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0)
        gl.glMatrixMode(GL2.GL_PROJECTION)
        gl.glLoadIdentity()
        gl.glMatrixMode(GL2.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glClearColor(0f, 0f, 0f, 1f)
        gl.glClear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT)

        gl.glViewport(0, 0, leftFBO.width, leftFBO.height)
        distortionShader.draw(gl, leftFBO.textureName)

        gl.glViewport(leftFBO.width, 0, rightFBO.width, rightFBO.height)
        distortionShader.draw(gl, rightFBO.textureName)
    }

    override fun getSceneDrawer(): SceneDrawer? {
        return drawer
    }

    override fun setSceneDrawer(d: SceneDrawer?) {
        this.drawer = d
    }

    override fun reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
        rightFBO.updateSize(drawable.gl.gL2, width / 2, height)
        leftFBO.updateSize(drawable.gl.gL2, width / 2, height)
    }

    override fun dispose(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL2
        distortionShader.release(gl)
        leftFBO.release(gl)
        rightFBO.release(gl)
    }
}