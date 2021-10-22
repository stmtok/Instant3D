package com.stmtok.gl

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2
import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.glu.GLU
import com.stmtok.gl.shader.NormalShader
import java.awt.Dimension


class NormalRenderer(
    width: Int, height: Int, fovy: Double = 32.0,
    val mode: Mode = Mode.Perspective
) : GLRenderer(fovy, 0.1, 1200.0) {
    enum class Mode {
        Perspective,
        Orthographic,
    }

    private var drawer: SceneDrawer? = null

    /** 並行投影のスケール */
    var scale: Double = 2.0

    private val glu = GLU()

    /** フレームバッファオブジェクト */
    private val fbo: FrameBufferObject = FrameBufferObject()

    /** 通常シェーダ */
    private val shader: NormalShader = NormalShader()

    init {
        preferredSize = Dimension(width, height)
        isFocusable = false
        addGLEventListener(this)
    }

    override fun getSceneDrawer(): SceneDrawer? {
        return drawer
    }

    override fun setSceneDrawer(d: SceneDrawer?) {
        this.drawer = d
    }

    override fun init(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL2
        fbo.setup(gl, drawable.surfaceWidth, drawable.surfaceHeight)
        shader.setup(gl)
    }

    override fun display(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL2
        val sceneDrawer = getSceneDrawer()
        sceneDrawer?.preDisplay(gl)

        gl.glEnable(GL2.GL_LIGHTING)
        gl.glEnable(GL2.GL_LIGHT0)
        gl.glEnable(GL2.GL_LIGHT1)
        gl.glEnable(GL.GL_DEPTH_TEST)

        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fbo.frameBufferName)
        gl.glMatrixMode(GL2.GL_PROJECTION)
        gl.glLoadIdentity()
        val ratio = fbo.width / fbo.height.toDouble()
        when (mode) {
            Mode.Perspective -> {
                glu.gluPerspective(fovy, ratio, near, far)
            }
            Mode.Orthographic -> {
                gl.glOrtho(-ratio * scale, ratio * scale, -scale, scale, near, far)
            }
        }
        gl.glMatrixMode(GL2.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glViewport(0, 0, fbo.width, fbo.height)

        sceneDrawer?.drawScene(gl) ?: kotlin.run {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT)
        }

        gl.glDisable(GL.GL_DEPTH_TEST)
        gl.glDisable(GL2.GL_LIGHT1)
        gl.glDisable(GL2.GL_LIGHT0)
        gl.glDisable(GL2.GL_LIGHTING)

        sceneDrawer?.preRender(gl)

        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0)
        gl.glMatrixMode(GL2.GL_PROJECTION)
        gl.glLoadIdentity()
        gl.glMatrixMode(GL2.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glClearColor(0f, 0f, 0f, 1f)
        gl.glClear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT)

        gl.glViewport(0, 0, fbo.width, fbo.height)
        shader.draw(gl, fbo.textureName)
    }


    override fun reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
        fbo.updateSize(drawable.gl.gL2, width, height)
    }

    override fun dispose(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL2
        shader.release(gl)
        fbo.release(gl)
    }
}