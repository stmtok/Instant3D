package com.stmtok.gl

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2
import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.glu.GLU
import com.jogamp.opengl.util.awt.TextRenderer
import com.stmtok.gl.shader.NormalShader
import java.awt.Color
import java.awt.Dimension
import java.awt.Font

class DimensionRenderer(width: Int, height: Int) : GLRenderer(32.0, 0.1, 100.0) {
    /** フレームバッファオブジェクト */
    private val topFBO = FrameBufferObject()
    private val frontFBO = FrameBufferObject()
    private val perspectiveFBO = FrameBufferObject()
    private val sideFBO = FrameBufferObject()

    /** 通常シェーダ */
    private val shader: NormalShader = NormalShader()

    /** 並行投影のスケール */
    var scale: Double = 6.0
    private var drawer: DimensionDrawer? = null
    private val fontSize = 16
    private val textRenderer = TextRenderer(Font(Font.SANS_SERIF, Font.PLAIN, fontSize))
    private val glu = GLU()

    init {
        preferredSize = Dimension(width, height)
        isFocusable = false
        addGLEventListener(this)
    }

    override fun getSceneDrawer(): DimensionDrawer? {
        return drawer
    }

    override fun setSceneDrawer(d: SceneDrawer?) {
        this.drawer = d as? DimensionDrawer
    }

    override fun init(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL2
        topFBO.setup(gl, width / 2, height / 2)
        frontFBO.setup(gl, width / 2, height / 2)
        perspectiveFBO.setup(gl, width / 2, height / 2)
        sideFBO.setup(gl, width / 2, height / 2)
        shader.setup(gl)
    }

    override fun reshape(drawable: GLAutoDrawable, p1: Int, p2: Int, p3: Int, p4: Int) {
        topFBO.updateSize(drawable.gl.gL2, width / 2, height / 2)
        frontFBO.updateSize(drawable.gl.gL2, width / 2, height / 2)
        perspectiveFBO.updateSize(drawable.gl.gL2, width / 2, height / 2)
        sideFBO.updateSize(drawable.gl.gL2, width / 2, height / 2)
    }

    override fun dispose(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL2
        shader.release(gl)
        topFBO.release(gl)
        frontFBO.release(gl)
        perspectiveFBO.release(gl)
        sideFBO.release(gl)
    }

    override fun display(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL2
        val sceneDrawer = getSceneDrawer()
        sceneDrawer?.preDisplay(gl)

        gl.glEnable(GL2.GL_LIGHTING)
        gl.glEnable(GL2.GL_LIGHT0)
        gl.glEnable(GL2.GL_LIGHT1)
        gl.glEnable(GL.GL_DEPTH_TEST)

        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, perspectiveFBO.frameBufferName)
        gl.glMatrixMode(GL2.GL_PROJECTION)
        gl.glLoadIdentity()
        val pRatio = perspectiveFBO.width / perspectiveFBO.height.toDouble()
        glu.gluPerspective(fovy, pRatio, near, far)
        gl.glMatrixMode(GL2.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glViewport(0, 0, perspectiveFBO.width, perspectiveFBO.height)

        sceneDrawer?.dimension = DimensionDrawer.Dimension.FREE
        sceneDrawer?.drawScene(gl) ?: kotlin.run {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT)
        }
        textRenderer.setColor(Color.WHITE)
        textRenderer.beginRendering(perspectiveFBO.width, perspectiveFBO.height)
        textRenderer.draw("Perspective", 10, perspectiveFBO.height - fontSize)
        textRenderer.endRendering()

        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, topFBO.frameBufferName)
        gl.glMatrixMode(GL2.GL_PROJECTION)
        gl.glLoadIdentity()
        val tRatio = topFBO.width / topFBO.height.toDouble()
        gl.glOrtho(-tRatio * scale, tRatio * scale, -scale, scale, -far, far)
        gl.glMatrixMode(GL2.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glViewport(0, 0, topFBO.width, topFBO.height)

        sceneDrawer?.dimension = DimensionDrawer.Dimension.TOP
        sceneDrawer?.drawScene(gl) ?: kotlin.run {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT)
        }
        textRenderer.setColor(Color.WHITE)
        textRenderer.beginRendering(topFBO.width, topFBO.height)
        textRenderer.draw("Top(X-Z)", 10, topFBO.height - fontSize)
        textRenderer.endRendering()

        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, frontFBO.frameBufferName)
        gl.glMatrixMode(GL2.GL_PROJECTION)
        gl.glLoadIdentity()
        val fRatio = frontFBO.width / frontFBO.height.toDouble()
        gl.glOrtho(-fRatio * scale, fRatio * scale, -scale, scale, -far, far)
        gl.glMatrixMode(GL2.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glViewport(0, 0, frontFBO.width, frontFBO.height)

        sceneDrawer?.dimension = DimensionDrawer.Dimension.FRONT
        sceneDrawer?.drawScene(gl) ?: kotlin.run {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT)
        }
        textRenderer.setColor(Color.WHITE)
        textRenderer.beginRendering(frontFBO.width, frontFBO.height)
        textRenderer.draw("Front(X-Y)", 10, frontFBO.height - fontSize)
        textRenderer.endRendering()

        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, sideFBO.frameBufferName)
        gl.glMatrixMode(GL2.GL_PROJECTION)
        gl.glLoadIdentity()
        val sRatio = sideFBO.width / sideFBO.height.toDouble()
        gl.glOrtho(-sRatio * scale, sRatio * scale, -scale, scale, -far, far)
        gl.glMatrixMode(GL2.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glViewport(0, 0, sideFBO.width, sideFBO.height)

        sceneDrawer?.dimension = DimensionDrawer.Dimension.SIDE
        sceneDrawer?.drawScene(gl) ?: kotlin.run {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT or GL.GL_DEPTH_BUFFER_BIT)
        }
        textRenderer.setColor(Color.WHITE)
        textRenderer.beginRendering(sideFBO.width, sideFBO.height)
        textRenderer.draw("Side(Z-Y)", 10, sideFBO.height - fontSize)
        textRenderer.endRendering()

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

        gl.glViewport(0, frontFBO.height, topFBO.width, topFBO.height)
        shader.draw(gl, topFBO.textureName)

        gl.glViewport(topFBO.width, frontFBO.height, perspectiveFBO.width, perspectiveFBO.height)
        shader.draw(gl, perspectiveFBO.textureName)

        gl.glViewport(0, 0, frontFBO.width, frontFBO.height)
        shader.draw(gl, frontFBO.textureName)

        gl.glViewport(frontFBO.width, 0, sideFBO.width, sideFBO.height)
        shader.draw(gl, sideFBO.textureName)
    }

}